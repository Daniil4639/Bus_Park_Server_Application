package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Driver;
import app.models.dto.drivers.DriverRequestDto;
import app.models.dto.drivers.DriverResponseDto;
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

    public List<DriverResponseDto> readAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverResponseDto::new)
                .toList();
    }

    public DriverResponseDto readDriverByLicenseNumber(String licenseNumber) {
        return new DriverResponseDto(driverRepository.findByLicenseNumber(licenseNumber).orElseThrow(
                () -> new NoDataException("No driver with license number: " + licenseNumber + "!")
        ));
    }

    public DriverResponseDto addDriver(DriverRequestDto driverDto) {
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
            return new DriverResponseDto(driverRepository.saveAndFlush(driver));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public DriverResponseDto updateDriver(UUID id, DriverRequestDto driverDto) {
        try {
            Driver updatableDriver = driverRepository.findById(id).get();

            updatableDriver.updateEntity(driverDto);

            return new DriverResponseDto(driverRepository.saveAndFlush(updatableDriver));
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