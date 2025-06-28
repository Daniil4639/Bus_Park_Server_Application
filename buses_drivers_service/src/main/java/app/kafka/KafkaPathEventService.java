package app.kafka;

import app.models.Bus;
import app.repositories.BusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaPathEventService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BusRepository busRepository;

    @Value("${spring.kafka.buses_paths_topic_name}")
    private String busesPathsTopicName;

    @KafkaListener(topics = "#{'${spring.kafka.paths_buses_topic_name}'}")
    public void pathDeleteEvent(String pathId) {
        try {
            UUID id = UUID.fromString(pathId);

            moveBusesToArchiveByPathId(id);
        } catch (Exception ex) {
            kafkaTemplate.send(busesPathsTopicName, pathId);
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