package app;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Driver;
import app.models.dto.drivers.DriverRequestDto;
import app.models.dto.drivers.DriverResponseDto;
import app.repositories.DriverRepository;
import app.services.DriverService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService service;

    @Test
    @DisplayName("Drivers - Service - Read Test - Correct")
    public void driverReadTestCorrect() {
        Driver driver1 = getDriverForTest("lic1");
        Driver driver2 = getDriverForTest("lic2");

        List<Driver> drivers = List.of(driver1, driver2);

        Mockito.when(driverRepository.findAll()).thenReturn(drivers);

        assertThat(service.readAllDrivers(), equalTo(drivers.stream()
                .map(DriverResponseDto::new).toList()));
    }

    @Test
    @DisplayName("Drivers - Service - Add test - Correct")
    public void driverAddTestCorrect() {
        String licenseNumber = "number";
        Driver driverRequest = getDriverForTest(licenseNumber);
        driverRequest.setId(null);
        Driver driverResponse = getDriverForTest(licenseNumber);

        Mockito.doNothing().when(driverRepository).rejectSoftDelete(licenseNumber);
        Mockito.when(driverRepository.findByLicenseNumber(licenseNumber)).thenReturn(Optional.empty());
        Mockito.when(driverRepository.saveAndFlush(driverRequest)).thenReturn(driverResponse);

        assertThat(service.addDriver(
                new DriverRequestDto("schedule", "fullName", 27, "phone",
                        "email", licenseNumber, "status")
        ), equalTo(new DriverResponseDto(driverResponse)));
    }

    @Test
    @DisplayName("Drivers - Service - Add test - Incorrect body")
    public void driverAddTestIncorrectBody() {
        String licenseNumber = "number";

        Mockito.doThrow(new IncorrectBodyException("")).when(driverRepository).rejectSoftDelete(licenseNumber);

        try {
            service.addDriver(new DriverRequestDto());

            assertThat(1, equalTo(2));
        } catch (IncorrectBodyException ignored) {}
    }

    @Test
    @DisplayName("Drivers - Service - Update test - Correct")
    public void driverUpdateTestCorrect() {
        String licenseNumber = "number";
        Driver driver = getDriverForTest(licenseNumber);
        Driver expectedDriver = getDriverForTest(licenseNumber);
        expectedDriver.setId(driver.getId());
        expectedDriver.setFullName("test_name");

        Mockito.when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        Mockito.when(driverRepository.saveAndFlush(expectedDriver)).thenReturn(expectedDriver);

        assertThat(service.updateDriver(driver.getId(), new DriverRequestDto(null,
                        "test_name", null, null, null,
                        null, null)),
                equalTo(new DriverResponseDto(expectedDriver)));
    }

    @Test
    @DisplayName("Drivers - Service - Update test - No data")
    public void driverUpdateTestNoData() {
        UUID testId = UUID.randomUUID();

        Mockito.when(driverRepository.findById(testId)).thenReturn(Optional.empty());

        try {
            service.updateDriver(testId, new DriverRequestDto(null, "test_name", null,
                    null, null, null, null));

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Drivers - Service - Delete test - Correct")
    public void driverDeleteTestCorrect() {
        Driver driver = getDriverForTest("number");

        Mockito.when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        Mockito.doNothing().when(driverRepository).deleteById(driver.getId());

        service.deleteDriver(driver.getId());
    }

    @Test
    @DisplayName("Drivers - Service - Delete test - No data")
    public void driverDeleteTestNoData() {
        UUID id = UUID.randomUUID();

        Mockito.when(driverRepository.findById(id)).thenReturn(Optional.empty());

        try {
            service.deleteDriver(id);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    private Driver getDriverForTest(String licenseNumber) {
        return new Driver(
                UUID.randomUUID(),
                "schedule",
                "fullName",
                27,
                "phone",
                "email",
                licenseNumber,
                "status",
                false
        );
    }
}