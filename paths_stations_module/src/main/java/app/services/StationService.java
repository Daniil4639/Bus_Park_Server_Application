package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.PathStation;
import app.models.Station;
import app.models.dto.paths.PathPreviewDto;
import app.models.dto.stations.StationCreateDto;
import app.models.dto.stations.StationDto;
import app.repositories.PathStationRepository;
import app.repositories.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final PathStationRepository pathStationRepository;

    public List<StationDto> readAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(StationDto::new)
                .toList();
    }

    public StationDto addStation(StationCreateDto stationDto) {
        try {
            stationRepository.rejectSoftDelete(
                    stationDto.getName(), stationDto.getAddress()
            );
            Optional<Station> returnedFromSoftDelete = stationRepository.findByNameAndAddress(
                    stationDto.getName(), stationDto.getAddress());

            if (returnedFromSoftDelete.isPresent()) {
                return new StationDto(returnedFromSoftDelete.get());
            }

            Station station = new Station(
                    null, stationDto.getName(),
                    stationDto.getAddress(), false
            );

            return new StationDto(stationRepository.saveAndFlush(station));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public StationDto updateStationInfo(StationDto stationDto) {
        UUID id = stationDto.getId();
        if (id == null) {
            throw new IncorrectBodyException("No station id!");
        }

        try {
            Station updatableStation = stationRepository.findById(id).get();

            updatableStation.updateEntity(stationDto);

            return new StationDto(stationRepository.saveAndFlush(updatableStation));
        } catch (NoSuchElementException ex) {
            throw new NoDataException("No station with id: " + id + "!");
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public List<PathPreviewDto> getPathsListByStationInfo(StationDto stationDto) {
        Optional<Station> station = stationRepository.findByNameAndAddress(
                stationDto.getName(), stationDto.getAddress()
        );

        if (station.isEmpty()) {
            throw new NoDataException("No station with name \"" + stationDto.getName()
                    + "\" and address \"" + stationDto.getAddress() + "\"!");
        }

        UUID stationId = station.get().getId();
        return pathStationRepository.findByStationId(stationId).stream()
                .map(PathStation::getPath)
                .map(PathPreviewDto::new)
                .toList();
    }

    @Transactional
    public void deleteStationById(UUID id) {
        Optional<Station> deletableStation = stationRepository.findById(id);

        if (deletableStation.isEmpty()) {
            throw new NoDataException("No station with id: " + id + "!");
        }
        Station station = deletableStation.get();

        for (PathStation pathStation: pathStationRepository.findByStationId(id)) {
            pathStationRepository.deleteById(pathStation.getId());
        }

        stationRepository.deleteById(id);
    }
}