package app.controllers;

import app.dto.paths.PathPreviewResponseDto;
import app.dto.paths.PathRequestDto;
import app.dto.paths.PathResponseDto;
import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.exceptions.ServiceErrorResponse;
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
@RequestMapping("/api/paths")
@RequiredArgsConstructor
public class PathsController {

    private final RateLimiterRegistry registry;

    private final WebClient pathsStationsServiceClient;

    private final static String PATHS_URI = "/api/v1/paths";

    @GetMapping
    public Mono<List<PathResponseDto>> getAllPaths() {
        return pathsStationsServiceClient.get()
                .uri(PATHS_URI)
                .retrieve()
                .bodyToFlux(PathResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .collectList();
    }

    @GetMapping("/path")
    public Mono<PathResponseDto> getPathByNumberAndCity(@RequestParam("number") String number,
                                                        @RequestParam("city") String city) {
        return pathsStationsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI + "/path")
                        .queryParam("number", number)
                        .queryParam("city", city)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(PathResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")));
    }

    @GetMapping("/by_station")
    public Mono<List<PathPreviewResponseDto>> getPathsByStation(@RequestParam("name") String name,
                                                                @RequestParam("address") String address) {
        return pathsStationsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI + "/by_station")
                        .queryParam("name", name)
                        .queryParam("address", address)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToFlux(PathPreviewResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .collectList();
    }

    @PostMapping
    public Mono<PathResponseDto> addPath(@RequestBody PathRequestDto pathDto) {
        return pathsStationsServiceClient.post()
                .uri(PATHS_URI)
                .body(pathDto, PathRequestDto.class)
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
                .bodyToMono(PathResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")));
    }

    @PutMapping
    public Mono<PathResponseDto> updatePath(@RequestParam("path_id") UUID id,
                                            @RequestBody PathRequestDto pathDto) {
        return pathsStationsServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI)
                        .queryParam("path_id", id)
                        .build())
                .body(pathDto, PathRequestDto.class)
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
                .bodyToMono(PathResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")));
    }

    @DeleteMapping
    public Mono<Void> deletePath(@RequestParam("path_id") UUID id) {
        return pathsStationsServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI)
                        .queryParam("path_id", id)
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
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")));
    }
}