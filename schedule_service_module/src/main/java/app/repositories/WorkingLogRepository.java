package app.repositories;

import app.models.WorkingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkingLogRepository extends JpaRepository<WorkingLog, UUID> {

    List<WorkingLog> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<WorkingLog> findAllByLicenseNumberAndStartTimeBetween(String licenseNumber,
                                                             LocalDateTime start, LocalDateTime end);

    void deleteAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}