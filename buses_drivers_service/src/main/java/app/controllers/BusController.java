package app.controllers;

import app.models.dto.BusCreateUpdateDto;
import app.models.dto.BusResponseDto;
import app.services.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buses")
@RequiredArgsConstructor
public class BusController {

    private final BusService service;

    @GetMapping
    public List<BusResponseDto> getAllBuses() {
        return service.readAllBuses();
    }

    @PostMapping
    public BusResponseDto createBus(@RequestBody BusCreateUpdateDto busDto) {
        return service.addBus(busDto);
    }

    @PutMapping
    public BusResponseDto updateBus(@RequestBody BusCreateUpdateDto busDto) {
        return service.updateBus(busDto);
    }

    @DeleteMapping
    public void deleteBus(@RequestParam("bus_id") UUID id) {
        service.deleteBus(id);
    }
}