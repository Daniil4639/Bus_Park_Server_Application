package app.controllers;

import app.dto.departments.DepartmentRequestDto;
import app.dto.departments.DepartmentResponseDto;
import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.services.UserRoleValidationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentsController {

    private final WebClient busesDriversServiceClient;

    private final UserRoleValidationService roleValidationService;

    private final static String DEPARTMENTS_URI = "/api/v1/departments";

    @RateLimiter(name = "departmentsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllDepartments() {
        return busesDriversServiceClient.get()
                .uri(DEPARTMENTS_URI)
                .retrieve()
                .bodyToFlux(DepartmentResponseDto.class)
                .map(roleValidationService::clearDepartment)
                .collectList()
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    @RateLimiter(name = "departmentsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PostMapping
    public Mono<ResponseEntity<Object>> addDepartment(@RequestBody DepartmentRequestDto departmentDto) {
        return busesDriversServiceClient.post()
                .uri(DEPARTMENTS_URI)
                .body(departmentDto, DepartmentRequestDto.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IncorrectBodyException(body)))
                )
                .bodyToMono(DepartmentResponseDto.class)
                .map(roleValidationService::clearDepartment)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(IncorrectBodyException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "departmentsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PutMapping
    public Mono<ResponseEntity<Object>> updateDepartment(@RequestParam("department_id") UUID id,
                                                        @RequestBody DepartmentRequestDto departmentDto) {
        return busesDriversServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(DEPARTMENTS_URI)
                        .queryParam("department_id", id)
                        .build())
                .body(departmentDto, DepartmentRequestDto.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IncorrectBodyException(body)))
                )
                .bodyToMono(DepartmentResponseDto.class)
                .map(roleValidationService::clearDepartment)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(NoDataException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())))
                .onErrorResume(IncorrectBodyException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "departmentsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @DeleteMapping
    public Mono<ResponseEntity<Object>> deleteDepartment(@RequestParam("department_id") UUID id) {
        return busesDriversServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DEPARTMENTS_URI)
                        .queryParam("department_id", id)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToMono(Void.class)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(NoDataException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    private Mono<ResponseEntity<Object>> tooManyRequestsMethod(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Too many requests to 'Departments' service!"));
    }
}