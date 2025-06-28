package app.repositories;

import app.models.PathStation;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@CacheConfig(cacheNames = "path_station")
public interface PathStationRepository extends JpaRepository<PathStation, UUID> {

    @CacheEvict(allEntries = true)
    PathStation save(PathStation pathStation);

    @CacheEvict(allEntries = true)
    PathStation saveAndFlush(PathStation pathStation);

    @Override
    @Cacheable(key = "'all'")
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<PathStation> findAll();

    @Cacheable(key = "{paths, #stationId}")
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.station.id = ?1")
    List<PathStation> findByStationId(UUID stationId);

    @Cacheable(key = "{stations, #pathId}")
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.path.id = ?1")
    List<PathStation> findByPathId(UUID pathId);

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.path.id = ?1 and b.station.id = ?2")
    Optional<PathStation> findByPathIdAndStationId(UUID pathId, UUID stationId);

    @Transactional
    @Override
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = false where b.path.id = ?1 and b.station.id = ?2")
    void rejectSoftDeleteByPathIdAndStationId(UUID pathId, UUID stationId);

    @Transactional
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = false where b.path.id = ?1")
    void rejectSoftDeleteByPathId(UUID pathId);
}