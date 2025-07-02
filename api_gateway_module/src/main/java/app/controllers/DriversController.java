package app.controllers;

import app.dto.drivers.DriverRequestDto;
import app.dto.drivers.DriverResponseDto;
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
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriversController {

    private final WebClient busesDriversServiceClient;

    private final UserRoleValidationService roleValidationService;

    private final static String DRIVERS_URI = "/api/v1/drivers";

    @RateLimiter(name = "driversRateLimiter")
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllDrivers() {
        return busesDriversServiceClient.get()
                .uri(DRIVERS_URI)
                .retrieve()
                .bodyToFlux(DriverResponseDto.class)
                .map(roleValidationService::clearDriver)
                .collectList()
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    @RateLimiter(name = "driversRateLimiter")
    @GetMapping("/by_license")
    public Mono<ResponseEntity<Object>> getDriverByLicense(@RequestParam("licenseNumber") String licenseNumber) {
        return busesDriversServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DRIVERS_URI + "/by_license")
                        .queryParam("licenseNumber", licenseNumber)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToMono(DriverResponseDto.class)
                .map(roleValidationService::clearDriver)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(NoDataException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "driversRateLimiter")
    @PostMapping
    public Mono<ResponseEntity<Object>> addDriver(@RequestBody DriverRequestDto driverDto) {
        return busesDriversServiceClient.post()
                .uri(DRIVERS_URI)
                .body(driverDto, DriverRequestDto.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IncorrectBodyException(body)))
                )
                .bodyToMono(DriverResponseDto.class)
                .map(roleValidationService::clearDriver)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(IncorrectBodyException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "driversRateLimiter")
    @PutMapping
    public Mono<ResponseEntity<Object>> updateDriver(@RequestParam("driver_id") UUID id,
                                                @RequestBody DriverRequestDto driverDto) {
        return busesDriversServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(DRIVERS_URI)
                        .queryParam("driver_id", id)
                        .build())
                .body(driverDto, DriverRequestDto.class)
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
                .bodyToMono(DriverResponseDto.class)
                .map(roleValidationService::clearDriver)
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

    @RateLimiter(name = "driversRateLimiter")
    @DeleteMapping
    public Mono<ResponseEntity<Object>> deleteDriver(@RequestParam("driver_id") UUID id) {
        return busesDriversServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DRIVERS_URI)
                        .queryParam("driver_id", id)
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
}