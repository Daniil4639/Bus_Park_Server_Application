package app.kafka;

import app.repositories.PathRepository;
import app.repositories.PathStationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaPathEventService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PathRepository pathRepository;
    private final PathStationRepository pathStationRepository;

    @Value("${spring.kafka.paths_buses_topic_name}")
    private String pathsBusesTopicName;

    public void sendPathDeleteEvent(UUID pathId) {
        kafkaTemplate.send(pathsBusesTopicName, pathId.toString());
    }

    @KafkaListener(topics = "#{'${spring.kafka.buses_paths_topic_name}'}")
    public void pathRollbackEvent(String pathId) {
        UUID id = UUID.fromString(pathId);

        rollbackByPathId(id);
    }

    @Transactional
    private void rollbackByPathId(UUID pathId) {
        pathRepository.rejectSoftDeleteById(pathId);

        pathStationRepository.rejectSoftDeleteByPathId(pathId);
    }
}