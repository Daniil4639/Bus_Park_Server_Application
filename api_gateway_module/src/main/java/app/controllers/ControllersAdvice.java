package app.controllers;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ControllersAdvice {

    @ExceptionHandler(NoDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<String> handleNoData(NoDataException ex) {
        return Mono.just(ex.getMessage());
    }

    @ExceptionHandler(IncorrectBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleNoData(IncorrectBodyException ex) {
        return Mono.just(ex.getMessage());
    }

    @ExceptionHandler(RequestNotPermitted.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Mono<String> handleTooManyRequests(RequestNotPermitted ex) {
        return Mono.just(ex.getMessage());
    }
}