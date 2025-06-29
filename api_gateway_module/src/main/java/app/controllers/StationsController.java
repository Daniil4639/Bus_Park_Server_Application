package app.controllers;

import app.dto.stations.StationRequestDto;
import app.dto.stations.StationResponseDto;
import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.exceptions.ServiceErrorResponse;
import app.services.UserRoleValidationService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationsController {

    private final RateLimiterRegistry registry;

    private final WebClient pathsStationsServiceClient;

    private final UserRoleValidationService roleValidationService;

    private final static String STATIONS_URI = "/api/v1/stations";

    @GetMapping
    public Mono<List<StationResponseDto>> getAllStations() {
        return pathsStationsServiceClient.get()
                .uri(STATIONS_URI)
                .retrieve()
                .bodyToFlux(StationResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("stationsService")))
                .map(roleValidationService::clearStation)
                .collectList();
    }

    @PostMapping
    public Mono<StationResponseDto> addStation(@RequestBody StationRequestDto stationDto) {
        return pathsStationsServiceClient.post()
                .uri(STATIONS_URI)
                .body(stationDto, StationRequestDto.class)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new IncorrectBodyException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(StationResponseDto.class)
                .map(roleValidationService::clearStation)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("stationsService")));
    }

    @PutMapping
    public Mono<StationResponseDto> updateStation(@RequestParam("station_id") UUID id,
                                                  @RequestBody StationRequestDto stationDto) {
        return pathsStationsServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(STATIONS_URI)
                        .queryParam("station_id", id)
                        .build())
                .body(stationDto, StationRequestDto.class)
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
                .bodyToMono(StationResponseDto.class)
                .map(roleValidationService::clearStation)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("stationsService")));
    }

    @DeleteMapping
    public Mono<Void> deleteStation(@RequestParam("station_id") UUID id) {
        return pathsStationsServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(STATIONS_URI)
                        .queryParam("station_id", id)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(Void.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("stationsService")));
    }
}