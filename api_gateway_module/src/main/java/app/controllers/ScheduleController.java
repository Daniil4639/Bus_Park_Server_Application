package app.controllers;

import app.dto.working_logs.WorkingLogResponseDto;
import app.exceptions.IncorrectBodyException;
import app.services.UserRoleValidationService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final WebClient scheduleServiceClient;

    private final UserRoleValidationService roleValidationService;

    private final static String SCHEDULE_URI = "/api/v1/schedule";

    @RateLimiter(name = "scheduleRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllLogsBetween(@RequestParam("startTime") LocalDateTime startTime,
                                                          @RequestParam("endTime") LocalDateTime endTime) {
        return scheduleServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SCHEDULE_URI)
                        .queryParam("start_time", startTime)
                        .queryParam("end_time", endTime)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IncorrectBodyException(body)))
                )
                .bodyToFlux(WorkingLogResponseDto.class)
                .map(roleValidationService::clearWorkingLog)
                .collectList()
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    @RateLimiter(name = "scheduleRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping("/by_license")
    public Mono<ResponseEntity<Object>> getAllLogsByLicenseBetween(@RequestParam("license") String license,
                                                                        @RequestParam("startTime") LocalDateTime startTime,
                                                                        @RequestParam("endTime") LocalDateTime endTime) {
        return scheduleServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SCHEDULE_URI + "/by_license")
                        .queryParam("license", license)
                        .queryParam("start_time", startTime)
                        .queryParam("end_time", endTime)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IncorrectBodyException(body)))
                )
                .bodyToFlux(WorkingLogResponseDto.class)
                .map(roleValidationService::clearWorkingLog)
                .collectList()
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(IncorrectBodyException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "scheduleRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PutMapping
    public Mono<ResponseEntity<Object>> updateSchedule() {
        return scheduleServiceClient.put()
                .uri(SCHEDULE_URI)
                .retrieve()
                .bodyToMono(Void.class)
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    private Mono<ResponseEntity<Object>> tooManyRequestsMethod(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Too many requests to 'Schedule' service!"));
    }
}