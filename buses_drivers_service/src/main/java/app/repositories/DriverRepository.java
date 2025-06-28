package app.repositories;

import app.models.Driver;
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
@CacheConfig(cacheNames = "drivers")
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    @Caching(put = @CachePut(key = "#result.licenseNumber"),
            evict = @CacheEvict(key = "'all'"))
    Driver save(Driver driver);

    @Caching(put = @CachePut(key = "#result.licenseNumber"),
            evict = @CacheEvict(key = "'all'"))
    Driver saveAndFlush(Driver driver);

    @Override
    @Cacheable(key = "'all'")
    @Query("select b from #{#entityName} b where b.isDeleted = false")
    List<Driver> findAll();

    @Override
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.id = ?1")
    Optional<Driver> findById(UUID id);

    @Cacheable(key = "'#licenseNumber'", unless = "#result.isEmpty()")
    @Query("select b from #{#entityName} b where b.isDeleted = false and b.licenseNumber = ?1")
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    @Transactional
    @Override
    @Modifying
    @CacheEvict(allEntries = true)
    @Query("update #{#entityName} b set b.isDeleted = true where b.id = ?1")
    void deleteById(UUID id);

    @Transactional
    @Modifying
    @CacheEvict(key = "'all'")
    @Query("update #{#entityName} b set b.isDeleted = false where b.licenseNumber = ?1")
    void rejectSoftDelete(String licenseNumber);
}