package app.controllers;

import app.dto.drivers.DriverRequestDto;
import app.dto.drivers.DriverResponseDto;
import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.exceptions.ServiceErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriversController {

    private final WebClient busesDriversServiceClient;

    private final static String DRIVERS_URI = "/api/v1/drivers";

    @GetMapping
    public Mono<List<DriverResponseDto>> getAllDrivers() {
        return busesDriversServiceClient.get()
                .uri(DRIVERS_URI)
                .retrieve()
                .bodyToFlux(DriverResponseDto.class)
                .collectList();
    }

    @GetMapping("/by_license")
    public Mono<DriverResponseDto> getDriverByLicense(@RequestParam("licenseNumber") String licenseNumber) {
        return busesDriversServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DRIVERS_URI + "/by_license")
                        .queryParam("licenseNumber", licenseNumber)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(DriverResponseDto.class);
    }

    @PostMapping
    public Mono<DriverResponseDto> addDriver(@RequestBody DriverRequestDto driverDto) {
        return busesDriversServiceClient.post()
                .uri(DRIVERS_URI)
                .body(driverDto, DriverRequestDto.class)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new IncorrectBodyException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(DriverResponseDto.class);
    }

    @PutMapping
    public Mono<DriverResponseDto> updateDriver(@RequestParam("driver_id") UUID id,
                                                @RequestBody DriverRequestDto driverDto) {
        return busesDriversServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(DRIVERS_URI)
                        .queryParam("driver_id", id)
                        .build())
                .body(driverDto, DriverRequestDto.class)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .onStatus(
                        status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new IncorrectBodyException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(DriverResponseDto.class);
    }

    @DeleteMapping
    public Mono<Void> deleteDriver(@RequestParam("driver_id") UUID id) {
        return busesDriversServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DRIVERS_URI)
                        .queryParam("driver_id", id)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(Void.class);
    }
}