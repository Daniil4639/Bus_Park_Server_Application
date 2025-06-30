package app.controllers;

import app.models.WorkingLog;
import app.services.WorkingLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final WorkingLogService service;

    @GetMapping
    public List<WorkingLog> getAllLogsBetween(@RequestParam("startTime") LocalDateTime startTime,
                                              @RequestParam("endTime") LocalDateTime endTime) {
        return service.getAllLogsBetween(startTime, endTime);
    }

    @GetMapping("/by_license")
    public List<WorkingLog> getAllLogsBetweenByLicense(@RequestParam("license") String license,
                                                       @RequestParam("startTime") LocalDateTime startTime,
                                                       @RequestParam("endTime") LocalDateTime endTime) {
        return service.getAllLongBetweenByLicense(license, startTime, endTime);
    }

    @PutMapping
    public void updateSchedule() {
        service.updateNextWeekSchedule();
    }
}