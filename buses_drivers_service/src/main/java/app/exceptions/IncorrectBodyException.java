package app.exceptions;

public class IncorrectBodyException extends RuntimeException {

    public IncorrectBodyException(String msg) {
        super(msg);
    }
}