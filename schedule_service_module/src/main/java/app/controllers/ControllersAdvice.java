package app.controllers;

import app.exceptions.NextWeekScheduleAlreadyExists;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllersAdvice {

    @ExceptionHandler(NextWeekScheduleAlreadyExists.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNoData() {
        return "Next week schedule already exists!";
    }
}