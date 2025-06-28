package app.repositories;

import app.models.Bus;
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
@CacheConfig(cacheNames = "buses")
public interface BusRepository extends JpaRepository<Bus, UUID> {

    @Caching(put = @CachePut(key = "#result.number"),
            evict = @CacheEvict(allEntries = true, beforeInvocation = true))
    Bus save(Bus bus);

    @Caching(put = @CachePut(key = "#result.number"),
            evict = @CacheEvict(allEntries = true, beforeInvocation = true))
    Bus saveAndFlush(Bus bus);

    @Override
    @Cacheable(key = "'all'")
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Bus> findAll();

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.department.id = ?1")
    List<Bus> findAllByDepartmentId(UUID departmentId);

    @Query("select b from #{#entityName} b where b.isDeleted = false and b.pathId = ?1")
    List<Bus> findAllByPathId(UUID pathId);

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Bus> findById(UUID id);

    @Cacheable(key = "#number")
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.number = ?1")
    Optional<Bus> findByNumber(String number);

    @Transactional
    @Override
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @CacheEvict(key = "'all'")
    @Query("update #{#entityName} b set b.isDeleted = false where b.number = ?1")
    void rejectSoftDelete(String number);
}