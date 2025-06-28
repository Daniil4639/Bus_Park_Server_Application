package app.repositories;

import app.models.Station;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@CacheConfig(cacheNames = "stations")
public interface StationRepository extends JpaRepository<Station, UUID> {

    @Caching(
            put = @CachePut(key = "{#result.name, #result.address}"),
            evict = @CacheEvict(allEntries = true, beforeInvocation = true)
    )
    Station save(Station station);

    @Caching(
            put = @CachePut(key = "{#result.name, #result.address}"),
            evict = @CacheEvict(allEntries = true, beforeInvocation = true)
    )
    Station saveAndFlush(Station station);

    @Override
    @Cacheable(key = "'all'")
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Station> findAll();

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Station> findById(UUID id);

    @Cacheable(key = "{#name, #address}")
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.name = ?1 and b.address = ?2")
    Optional<Station> findByNameAndAddress(String name, String address);

    @Transactional
    @Override
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @CacheEvict(key = "'all'")
    @Query("update #{#entityName} b set b.isDeleted = false where b.name = ?1 and b.address = ?2")
    void rejectSoftDelete(String name, String address);
}