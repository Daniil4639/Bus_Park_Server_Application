package app.repositories;

import app.models.Station;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Station> findAll();

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Station> findById(UUID id);

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.name = ?1 and b.address = ?2")
    Optional<Station> findByNameAndAddress(String name, String address);

    @Transactional
    @Modifying
    @Query("update #{#entityName} b set b.isDeleted = false where b.name = ?1 and b.address = ?2")
    void rejectSoftDelete(String name, String address);
}