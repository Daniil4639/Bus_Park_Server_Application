package app.controllers;

import app.models.dto.paths.PathPreviewDto;
import app.models.dto.stations.StationCreateDto;
import app.models.dto.stations.StationDto;
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
    public List<StationDto> getAllStations() {
        return service.readAllStations();
    }

    @GetMapping("/paths_by_station")
    public List<PathPreviewDto> getPathsWithCurrentStation(@RequestBody StationDto stationDto) {
        return service.getPathsListByStationInfo(stationDto);
    }

    @PostMapping
    public StationDto addStation(@RequestBody StationCreateDto stationDto) {
        return service.addStation(stationDto);
    }

    @PutMapping
    public StationDto updateStation(@RequestBody StationDto stationDto) {
        return service.updateStationInfo(stationDto);
    }

    @DeleteMapping
    public void deleteStation(@RequestParam("station_id") UUID id) {
        service.deleteStationById(id);
    }
}