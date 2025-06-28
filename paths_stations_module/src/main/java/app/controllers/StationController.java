package app.controllers;

import app.models.dto.stations.StationRequestDto;
import app.models.dto.stations.StationResponseDto;
import app.services.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService service;

    @GetMapping
    public List<StationResponseDto> getAllStations() {
        return service.readAllStations();
    }

    @PostMapping
    public StationResponseDto addStation(@RequestBody StationRequestDto stationDto) {
        return service.addStation(stationDto);
    }

    @PutMapping
    public StationResponseDto updateStation(@RequestParam("station_id") UUID id,
                                            @RequestBody StationRequestDto stationDto) {
        return service.updateStationInfo(id, stationDto);
    }

    @DeleteMapping
    public void deleteStation(@RequestParam("station_id") UUID id) {
        service.deleteStationById(id);
    }
}