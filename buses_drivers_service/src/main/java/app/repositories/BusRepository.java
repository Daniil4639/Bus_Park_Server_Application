package app.repositories;

import app.models.Bus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusRepository extends JpaRepository<Bus, UUID> {

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Bus> findAll();

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.department.id = ?1")
    List<Bus> findAllByDepartmentId(UUID departmentId);

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Bus> findById(UUID id);

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.number = ?1")
    Optional<Bus> findByNumber(String number);

    @Transactional
    @Override
    @Modifying
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @Query("update #{#entityName} b set b.isDeleted = false where b.number = ?1")
    void rejectSoftDelete(String number);
}