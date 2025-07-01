package app.kafka;

import app.models.Bus;
import app.models.dto.drivers.DriverScheduleDto;
import app.repositories.BusRepository;
import app.services.DriverService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class KafkaEventService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BusRepository busRepository;
    private final DriverService driverService;
    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = Logger.getLogger(KafkaEventService.class.getName());

    @Value("${spring.kafka.buses_paths_topic_name}")
    private String busesPathsTopicName;

    @Value("${spring.kafka.schedule_from_drivers_topic_name}")
    private String fromDriversTopicName;

    @KafkaListener(topics = "#{'${spring.kafka.paths_buses_topic_name}'}")
    public void pathDeleteEvent(String pathId) {
        try {
            UUID id = UUID.fromString(pathId);

            moveBusesToArchiveByPathId(id);
        } catch (Exception ex) {
            kafkaTemplate.send(busesPathsTopicName, pathId);
        }
    }

    @KafkaListener(topics = "#{'${spring.kafka.schedule_from_log_topic_name}'}")
    public void scheduleRequestEvent(String message) throws JsonProcessingException {
        LOGGER.info(message);

        List<DriverScheduleDto> drivers = driverService.readAllDrivers().stream()
                .map(DriverScheduleDto::new)
                .toList();

        for (DriverScheduleDto driver: drivers) {
            kafkaTemplate.send(fromDriversTopicName, objectMapper.writeValueAsString(driver));
        }
    }

    @Transactional
    private void moveBusesToArchiveByPathId(UUID id) {
        List<Bus> buses = busRepository.findAllByPathId(id);

        for (Bus bus: buses) {
            bus.setPathId(null);
            bus.setStatus("ARCHIVED");
        }
    }
}