package app.controllers;

import app.dto.paths.PathPreviewResponseDto;
import app.dto.paths.PathRequestDto;
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
@RequestMapping("/api/paths")
@RequiredArgsConstructor
public class PathsController {

    private final WebClient pathsStationsServiceClient;

    private final UserRoleValidationService roleValidationService;

    private final static String PATHS_URI = "/api/v1/paths";

    @RateLimiter(name = "pathsRateLimiter")
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllPaths() {
        return pathsStationsServiceClient.get()
                .uri(PATHS_URI)
                .retrieve()
                .bodyToFlux(PathResponseDto.class)
                .map(roleValidationService::clearPath)
                .collectList()
                .flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)));
    }

    @RateLimiter(name = "pathsRateLimiter")
    @GetMapping("/path")
    public Mono<ResponseEntity<Object>> getPathByNumberAndCity(@RequestParam("number") String number,
                                                       @RequestParam("city") String city) {
        return pathsStationsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI + "/path")
                        .queryParam("number", number)
                        .queryParam("city", city)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToMono(PathResponseDto.class)
                .map(roleValidationService::clearPath)
                .<ResponseEntity<Object>>flatMap(body -> Mono.just(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)))
                .onErrorResume(NoDataException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.TEXT_PLAIN)
                                .body(ex.getMessage())));
    }

    @RateLimiter(name = "pathsRateLimiter")
    @GetMapping("/by_station")
    public Mono<ResponseEntity<Object>> getPathsByStation(@RequestParam("name") String name,
                                                                @RequestParam("address") String address) {
        return pathsStationsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI + "/by_station")
                        .queryParam("name", name)
                        .queryParam("address", address)
                        .build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NoDataException(body)))
                )
                .bodyToFlux(PathPreviewResponseDto.class)
                .map(roleValidationService::clearPathPreview)
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

    @RateLimiter(name = "pathsRateLimiter")
    @PostMapping
    public Mono<ResponseEntity<Object>> addPath(@RequestBody PathRequestDto pathDto) {
        return pathsStationsServiceClient.post()
                .uri(PATHS_URI)
                .body(pathDto, PathRequestDto.class)
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
                .bodyToMono(PathResponseDto.class)
                .map(roleValidationService::clearPath)
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

    @RateLimiter(name = "pathsRateLimiter")
    @PutMapping
    public Mono<ResponseEntity<Object>> updatePath(@RequestParam("path_id") UUID id,
                                            @RequestBody PathRequestDto pathDto) {
        return pathsStationsServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI)
                        .queryParam("path_id", id)
                        .build())
                .body(pathDto, PathRequestDto.class)
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
                .bodyToMono(PathResponseDto.class)
                .map(roleValidationService::clearPath)
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

    @RateLimiter(name = "pathsRateLimiter")
    @DeleteMapping
    public Mono<ResponseEntity<Object>> deletePath(@RequestParam("path_id") UUID id) {
        return pathsStationsServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PATHS_URI)
                        .queryParam("path_id", id)
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