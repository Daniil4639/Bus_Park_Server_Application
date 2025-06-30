package app.exceptions;

public class NextWeekScheduleAlreadyExists extends RuntimeException {

    public NextWeekScheduleAlreadyExists(String msg) {
        super(msg);
    }
}