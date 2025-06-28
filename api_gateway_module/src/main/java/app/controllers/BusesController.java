package app.controllers;

import app.dto.buses.BusRequestDto;
import app.dto.buses.BusResponseDto;
import app.dto.buses.BusResponseWithPathDto;
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
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusesController {

    private final RateLimiterRegistry registry;

    private final WebClient busesDriversServiceClient;
    private final WebClient pathsStationsServiceClient;

    private final static String BUSES_URI = "/api/v1/buses";
    private final static String PATHS_URI = "/api/v1/paths";

    @GetMapping
    public Mono<List<BusResponseWithPathDto>> getAllBuses() {
        return busesDriversServiceClient.get()
                .uri(BUSES_URI)
                .retrieve()
                .bodyToFlux(BusResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .flatMap(this::getBusWithPath)
                .collectList();
    }

    @GetMapping("/by_department")
    public Mono<List<BusResponseWithPathDto>> getBusesByDepartment(@RequestParam("name") String name,
                                                                   @RequestParam("address") String address) {
        return busesDriversServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI + "/by_department")
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
                .bodyToFlux(BusResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .flatMap(this::getBusWithPath)
                .collectList();
    }

    @GetMapping("/by_number")
    public Mono<BusResponseWithPathDto> getBusByNumber(@RequestParam("number") String number) {
        return busesDriversServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI + "/by_number")
                        .queryParam("number", number)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(ServiceErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new NoDataException(
                                        errorBody.getMessage()
                                )))
                )
                .bodyToMono(BusResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .flatMap(this::getBusWithPath);
    }

    @PostMapping
    public Mono<BusResponseWithPathDto> addBus(@RequestBody BusRequestDto busDto) {
        return busesDriversServiceClient.post()
                .uri(BUSES_URI)
                .body(busDto, BusRequestDto.class)
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
                .bodyToMono(BusResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .flatMap(this::getBusWithPath);
    }

    @PutMapping
    public Mono<BusResponseWithPathDto> updateBus(@RequestParam("bus_id") UUID id,
                                                  @RequestBody BusRequestDto busDto) {
        return busesDriversServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI)
                        .queryParam("bus_id", id)
                        .build())
                .body(busDto, BusRequestDto.class)
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
                .bodyToMono(BusResponseDto.class)
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("busesService")))
                .flatMap(this::getBusWithPath);
    }

    @DeleteMapping
    public Mono<Void> deleteBus(@RequestParam("bus_id") UUID id) {
        return busesDriversServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI)
                        .queryParam("bus_id")
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

    private Mono<BusResponseWithPathDto> getBusWithPath(BusResponseDto busDto) {
        return pathsStationsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI + "/by_id")
                        .queryParam("path_id", busDto.getPathId())
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
                .transformDeferred(RateLimiterOperator.of(registry.rateLimiter("pathsService")))
                .map(pathDto -> new BusResponseWithPathDto(busDto, pathDto));
    }
}