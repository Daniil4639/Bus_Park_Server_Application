package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Driver;
import app.models.dto.DriverDto;
import app.repositories.DriverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public List<DriverDto> readAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverDto::new)
                .toList();
    }

    public DriverDto addDriver(DriverDto driverDto) {
        try {
            driverRepository.rejectSoftDelete(driverDto.getLicenseNumber());
            Optional<Driver> optionalDriver = driverRepository.findByLicenseNumber(
                    driverDto.getLicenseNumber());

            Driver driver = optionalDriver.orElse(new Driver());

            driver.setFullName(driverDto.getFullName());
            driver.setAge(driverDto.getAge());
            driver.setPhone(driverDto.getPhone());
            driver.setEmail(driverDto.getEmail());
            driver.setLicenseNumber(driverDto.getLicenseNumber());
            driver.setStatus(driverDto.getStatus());
            return new DriverDto(driverRepository.saveAndFlush(driver));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public DriverDto updateDriver(DriverDto driverDto) {
        UUID id = driverDto.getId();
        if (id == null) {
            throw new IncorrectBodyException("No driver id!");
        }

        try {
            Driver updatableDriver = driverRepository.findById(id).get();

            updatableDriver.updateEntity(driverDto);

            return new DriverDto(driverRepository.saveAndFlush(updatableDriver));
        } catch (NoSuchElementException ex) {
            throw new NoDataException("No driver with id: " + id + "!");
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteDriver(UUID id) {
        Optional<Driver> deletableDriver = driverRepository.findById(id);

        if (deletableDriver.isEmpty()) {
            throw new NoDataException("No driver with id: " + id + "!");
        }
        Driver driver = deletableDriver.get();

        driverRepository.deleteById(id);
    }
}