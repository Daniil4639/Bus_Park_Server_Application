package app.controllers;

import app.models.dto.DriverDto;
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
    public List<DriverDto> readAllDrivers() {
        return service.readAllDrivers();
    }

    @PostMapping
    public DriverDto addDriver(@RequestBody DriverDto driverDto) {
        return service.addDriver(driverDto);
    }

    @PutMapping
    public DriverDto updateDriver(@RequestBody DriverDto driverDto) {
        return service.updateDriver(driverDto);
    }

    @DeleteMapping
    public void deleteDriver(@RequestParam("driver_id") UUID id) {
        service.deleteDriver(id);
    }
}