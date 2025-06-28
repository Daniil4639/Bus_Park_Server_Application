package app.controllers;

import app.dto.departments.DepartmentRequestDto;
import app.dto.departments.DepartmentResponseDto;
import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentsController {

    private final RateLimiterRegistry registry;

    private final WebClient busesDriversServiceClient;

    private final static String DEPARTMENTS_URI = "/api/v1/departments";

    @GetMapping
    public Mono<List<DepartmentResponseDto>> getAllDepartments() {
        return busesDriversServiceClient.get()
                .uri(DEPARTMENTS_URI)
                .retrieve()
                .bodyToFlux(DepartmentResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("departmentsService")))
                .collectList();
    }

    @PostMapping
    public Mono<DepartmentResponseDto> addDepartment(@RequestBody DepartmentRequestDto departmentDto) {
        return busesDriversServiceClient.post()
                .uri(DEPARTMENTS_URI)
                .body(departmentDto, DepartmentRequestDto.class)
                .retrieve()
                .bodyToMono(DepartmentResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("departmentsService")));
    }

    @PutMapping
    public Mono<DepartmentResponseDto> updateDepartment(@RequestParam("department_id") UUID id,
                                                        @RequestBody DepartmentRequestDto departmentDto) {
        return busesDriversServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(DEPARTMENTS_URI)
                        .queryParam("department_id", id)
                        .build())
                .body(departmentDto, DepartmentRequestDto.class)
                .retrieve()
                .bodyToMono(DepartmentResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("departmentsService")));
    }

    @DeleteMapping
    public Mono<Void> deleteDepartment(@RequestParam("department_id") UUID id) {
        return busesDriversServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DEPARTMENTS_URI)
                        .queryParam("department_id", id)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("departmentsService")));
    }
}