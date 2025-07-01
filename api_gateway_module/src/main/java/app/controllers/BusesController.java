package app.controllers;

import app.dto.buses.BusRequestDto;
import app.dto.buses.BusResponseDto;
import app.dto.buses.BusResponseWithPathDto;
import app.dto.paths.PathResponseDto;
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
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusesController {

    private final WebClient busesDriversServiceClient;
    private final WebClient pathsStationsServiceClient;
    private final UserRoleValidationService roleValidationService;

    private final static String BUSES_URI = "/api/v1/buses";
    private final static String PATHS_URI = "/api/v1/paths";

    @RateLimiter(name = "busesRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllBuses() {
        return busesDriversServiceClient.get()
                .uri(BUSES_URI)
                .retrieve()
                .bodyToFlux(BusResponseDto.class)
                .flatMap(this::getBusWithPath)
                .map(roleValidationService::clearBus)
                .collectList()
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    @RateLimiter(name = "busesRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping("/by_department")
    public Mono<ResponseEntity<Object>> getBusesByDepartment(@RequestParam("name") String name,
                                                                   @RequestParam("address") String address) {
        return busesDriversServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI + "/by_department")
                        .queryParam("name", name)
                        .queryParam("address", address)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToFlux(BusResponseDto.class)
                .flatMap(this::getBusWithPath)
                .map(roleValidationService::clearBus)
                .collectList()
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(NoDataException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "busesRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @GetMapping("/by_number")
    public Mono<ResponseEntity<Object>> getBusByNumber(@RequestParam("number") String number) {
        return busesDriversServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI + "/by_number")
                        .queryParam("number", number)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToMono(BusResponseDto.class)
                .flatMap(this::getBusWithPath)
                .map(roleValidationService::clearBus)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(NoDataException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "busesRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PostMapping
    public Mono<ResponseEntity<Object>> addBus(@RequestBody BusRequestDto busDto) {
        return busesDriversServiceClient.post()
                .uri(BUSES_URI)
                .body(busDto, BusRequestDto.class)
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
                .bodyToMono(BusResponseDto.class)
                .flatMap(this::getBusWithPath)
                .map(roleValidationService::clearBus)
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

    @RateLimiter(name = "busesRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @PutMapping
    public Mono<ResponseEntity<Object>> updateBus(@RequestParam("bus_id") UUID id,
                                                  @RequestBody BusRequestDto busDto) {
        return busesDriversServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI)
                        .queryParam("bus_id", id)
                        .build())
                .body(busDto, BusRequestDto.class)
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
                .bodyToMono(BusResponseDto.class)
                .flatMap(this::getBusWithPath)
                .map(roleValidationService::clearBus)
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

    @RateLimiter(name = "busesRateLimiter", fallbackMethod = "tooManyRequestsMethod")
    @DeleteMapping
    public Mono<ResponseEntity<Object>> deleteBus(@RequestParam("bus_id") UUID id) {
        return busesDriversServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(BUSES_URI)
                        .queryParam("bus_id")
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

    private Mono<BusResponseWithPathDto> getBusWithPath(BusResponseDto busDto) {
        return pathsStationsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI + "/by_id")
                        .queryParam("path_id", busDto.getPathId())
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToMono(PathResponseDto.class)
                .map(roleValidationService::clearPath)
                .map(pathDto -> new BusResponseWithPathDto(busDto, pathDto));
    }

    private Mono<ResponseEntity<Object>> tooManyRequestsMethod(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Too many requests to 'Buses' service!"));
    }
}