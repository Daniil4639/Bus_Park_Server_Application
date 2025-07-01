package app.controllers;

import app.models.dto.buses.BusRequestDto;
import app.models.dto.buses.BusResponseDto;
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

    @GetMapping("/by_department")
    public List<BusResponseDto> getAllBusesByDepartment(@RequestParam("name") String name,
                                                        @RequestParam("address") String address) {
        return service.readAllBusesByDepartment(name, address);
    }

    @GetMapping("/by_number")
    public BusResponseDto getBusByNumber(@RequestParam("number") String number) {
        return service.readBusByNumber(number);
    }

    @PostMapping
    public BusResponseDto createBus(@RequestBody BusRequestDto busDto) {
        return service.addBus(busDto);
    }

    @PutMapping
    public BusResponseDto updateBus(@RequestParam("bus_id") UUID id,
                                    @RequestBody BusRequestDto busDto) {
        return service.updateBus(id, busDto);
    }

    @DeleteMapping
    public void deleteBus(@RequestParam("bus_id") UUID id) {
        service.deleteBus(id);
    }
}