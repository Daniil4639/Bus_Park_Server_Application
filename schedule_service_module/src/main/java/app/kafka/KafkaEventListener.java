package app.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProcessService service;

    @Value("${spring.kafka.schedule_from_log_topic_name}")
    private String fromLogTopicName;

    @Transactional
    @KafkaListener(topics = "#{'${spring.kafka.schedule_from_drivers_topic_name}'}")
    public void scheduleEventHandler(String message) throws JsonProcessingException {
        service.createSchedule(message);
    }

    public void sendScheduleRequest() {
        kafkaTemplate.send(fromLogTopicName, "Request: get drivers with schedule!");
    }
}