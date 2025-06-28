package app.repositories;

import app.models.Path;
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
@CacheConfig(cacheNames = "paths")
public interface PathRepository extends JpaRepository<Path, UUID> {

    @Caching(
            put = @CachePut(key = "{#result.number, #result.city}"),
            evict = @CacheEvict(allEntries = true, beforeInvocation = true)
    )
    Path save(Path path);

    @Caching(
            put = @CachePut(key = "{#result.number, #result.city}"),
            evict = @CacheEvict(allEntries = true, beforeInvocation = true)
    )
    Path saveAndFlush(Path path);

    @Override
    @Cacheable(key = "'all'")
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Path> findAll();

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Path> findById(UUID id);

    @Cacheable(key = "{#number, #city}")
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.number = ?1 and b.city = ?2")
    Optional<Path> findByNumberAndCity(String number, String city);

    @Transactional
    @Override
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @CacheEvict(key = "'all'")
    @Query("update #{#entityName} b set b.isDeleted = false where b.number = ?1 and b.city = ?2")
    void rejectSoftDeleteByNumberAndCity(String number, String city);

    @Transactional
    @Modifying
    @CacheEvict(key = "'all'")
    @Query("update #{#entityName} b set b.isDeleted = false where b.id = ?1")
    void rejectSoftDeleteById(UUID pathId);
}