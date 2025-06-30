package app.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final WorkingLogService service;

    @Scheduled(cron = "0 0 21 ? * SAT")
    public void generateNextWeekSchedule() {
        service.generateNextWeekSchedule();
    }
}