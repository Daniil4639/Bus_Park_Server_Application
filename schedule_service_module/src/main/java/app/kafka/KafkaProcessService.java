package app.kafka;

import app.models.WorkingLog;
import app.models.dto.DriverScheduleDto;
import app.repositories.WorkingLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class KafkaProcessService {

    private final ObjectMapper objectMapper;
    private final WorkingLogRepository repository;

    private static final Logger LOGGER = Logger.getLogger(KafkaProcessService.class.getName());

    public void createSchedule(String message) throws JsonProcessingException {
        DriverScheduleDto driverDto = objectMapper.readValue(message, DriverScheduleDto.class);

        LocalDateTime startTimeForDay = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .toLocalDate()
                .atStartOfDay();

        try {
            String[] scheduleByDays = driverDto.getSchedule()
                    .replace(" ", "")
                    .split(";");

            for (String scheduleByDay: scheduleByDays) {
                String[] parts = scheduleByDay.split("[:,-]+");

                if (parts[1].equals("OFF")) {
                    startTimeForDay = startTimeForDay.plusDays(1);
                    continue;
                }

                for (int i = 1; i < parts.length; i += 2) {
                    int startTimeNumber = Integer.parseInt(parts[i]);
                    int endTimeNumber = Integer.parseInt(parts[i + 1]);

                    LocalDateTime startTime = startTimeForDay.plusHours(startTimeNumber);
                    LocalDateTime endTime = startTimeForDay.plusHours(endTimeNumber);
                    if (endTimeNumber <= startTimeNumber) {
                        endTime = endTime.plusDays(1);
                    }

                    repository.saveAndFlush(WorkingLog.builder()
                            .licenseNumber(driverDto.getLicenseNumber())
                            .phone(driverDto.getPhone())
                            .email(driverDto.getEmail())
                            .startTime(startTime)
                            .endTime(endTime)
                            .build());
                }

                startTimeForDay = startTimeForDay.plusDays(1);
            }
        } catch (Exception ex) {
            LOGGER.info("Incorrect schedule format: " + ex);
        }
    }
}