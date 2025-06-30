package app.controllers;

import app.dto.stations.StationRequestDto;
import app.dto.stations.StationResponseDto;
import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.services.UserRoleValidationService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
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
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationsController {

    private final RateLimiterRegistry registry;

    private final WebClient pathsStationsServiceClient;

    private final UserRoleValidationService roleValidationService;

    private final static String STATIONS_URI = "/api/v1/stations";

    @RateLimiter(name = "stationsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllStations() {
        return pathsStationsServiceClient.get()
                .uri(STATIONS_URI)
                .retrieve()
                .bodyToFlux(StationResponseDto.class)
                .map(roleValidationService::clearStation)
                .collectList()
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    @RateLimiter(name = "stationsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PostMapping
    public Mono<ResponseEntity<Object>> addStation(@RequestBody StationRequestDto stationDto) {
        return pathsStationsServiceClient.post()
                .uri(STATIONS_URI)
                .body(stationDto, StationRequestDto.class)
                .retrieve()
                .onStatus(status -> status == HttpStatus.BAD_REQUEST,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new IncorrectBodyException(body)))
                )
                .bodyToMono(StationResponseDto.class)
                .map(roleValidationService::clearStation)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(IncorrectBodyException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "stationsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PutMapping
    public Mono<ResponseEntity<Object>> updateStation(@RequestParam("station_id") UUID id,
                                                  @RequestBody StationRequestDto stationDto) {
        return pathsStationsServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(STATIONS_URI)
                        .queryParam("station_id", id)
                        .build())
                .body(stationDto, StationRequestDto.class)
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
                .bodyToMono(StationResponseDto.class)
                .map(roleValidationService::clearStation)
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

    @RateLimiter(name = "stationsRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @DeleteMapping
    public Mono<ResponseEntity<Object>> deleteStation(@RequestParam("station_id") UUID id) {
        return pathsStationsServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(STATIONS_URI)
                        .queryParam("station_id", id)
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
                .body("Too many requests to 'Stations' service!"));
    }
}