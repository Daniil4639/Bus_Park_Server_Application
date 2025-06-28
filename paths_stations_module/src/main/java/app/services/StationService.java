package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.PathStation;
import app.models.Station;
import app.models.dto.stations.StationRequestDto;
import app.models.dto.stations.StationResponseDto;
import app.repositories.PathStationRepository;
import app.repositories.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final PathStationRepository pathStationRepository;

    public List<StationResponseDto> readAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(StationResponseDto::new)
                .toList();
    }

    public StationResponseDto addStation(StationRequestDto stationDto) {
        try {
            stationRepository.rejectSoftDelete(
                    stationDto.getName(), stationDto.getAddress()
            );
            Optional<Station> returnedFromSoftDelete = stationRepository.findByNameAndAddress(
                    stationDto.getName(), stationDto.getAddress());

            if (returnedFromSoftDelete.isPresent()) {
                return new StationResponseDto(returnedFromSoftDelete.get());
            }

            Station station = new Station(
                    null, stationDto.getName(),
                    stationDto.getAddress(), false
            );

            return new StationResponseDto(stationRepository.saveAndFlush(station));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public StationResponseDto updateStationInfo(UUID id, StationRequestDto stationDto) {
        try {
            Station updatableStation = stationRepository.findById(id).get();

            updatableStation.updateEntity(stationDto);

            return new StationResponseDto(stationRepository.saveAndFlush(updatableStation));
        } catch (NoSuchElementException ex) {
            throw new NoDataException("No station with id: " + id + "!");
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
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