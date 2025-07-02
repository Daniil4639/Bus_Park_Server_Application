package app.services;

import app.models.WorkingLog;
import app.kafka.KafkaEventListener;
import app.repositories.WorkingLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingLogService {

    private final WorkingLogRepository workingLogRepository;
    private final KafkaEventListener eventService;

    public List<WorkingLog> getAllLogsBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return workingLogRepository.findAllByStartTimeBetween(
                startTime, endTime
        );
    }

    public List<WorkingLog> getAllLongBetweenByLicense(String license,
                                                       LocalDateTime startTime, LocalDateTime endTime) {
        return workingLogRepository.findAllByLicenseNumberAndStartTimeBetween(
                license, startTime, endTime
        );
    }

    @Transactional
    public void updateNextWeekSchedule() {
        LocalDateTime[] nextWeekBoarders = getStartAndEntTimesForNextWeek();

        workingLogRepository.deleteAllByStartTimeBetween(
                nextWeekBoarders[0], nextWeekBoarders[1]
        );

        eventService.sendScheduleRequest();
    }

    public void generateNextWeekSchedule() {
        LocalDateTime[] nextWeekBoarders = getStartAndEntTimesForNextWeek();

        if (getAllLogsBetween(nextWeekBoarders[0], nextWeekBoarders[1]).isEmpty()) {
            eventService.sendScheduleRequest();
        }
    }

    private LocalDateTime[] getStartAndEntTimesForNextWeek() {
        LocalDateTime startTime = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();
        LocalDateTime endTime = startTime.plusDays(7);

        return new LocalDateTime[] {startTime, endTime};
    }
}