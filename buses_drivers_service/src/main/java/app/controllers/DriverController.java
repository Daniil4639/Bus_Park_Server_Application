package app.controllers;

import app.models.dto.drivers.DriverRequestDto;
import app.models.dto.drivers.DriverResponseDto;
import app.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @GetMapping
    public List<DriverResponseDto> readAllDrivers() {
        return service.readAllDrivers();
    }

    @GetMapping("/by_license")
    public DriverResponseDto readDriverByLicenseNumber(@RequestParam("licenseNumber") String licenseNumber) {
        return service.readDriverByLicenseNumber(licenseNumber);
    }

    @PostMapping
    public DriverResponseDto addDriver(@RequestBody DriverRequestDto driverDto) {
        return service.addDriver(driverDto);
    }

    @PutMapping
    public DriverResponseDto updateDriver(@RequestParam("driver_id") UUID id,
                                          @RequestBody DriverRequestDto driverDto) {
        return service.updateDriver(id, driverDto);
    }

    @DeleteMapping
    public void deleteDriver(@RequestParam("driver_id") UUID id) {
        service.deleteDriver(id);
    }
}