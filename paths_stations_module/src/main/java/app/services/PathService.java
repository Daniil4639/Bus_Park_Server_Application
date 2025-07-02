package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.kafka.KafkaPathEventService;
import app.models.Path;
import app.models.PathStation;
import app.models.Station;
import app.models.dto.paths.PathPreviewResponseDto;
import app.models.dto.paths.PathRequestDto;
import app.models.dto.paths.PathResponseDto;
import app.repositories.PathRepository;
import app.repositories.PathStationRepository;
import app.repositories.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathService {

    private final PathRepository pathRepository;
    private final PathStationRepository pathStationRepository;
    private final StationRepository stationRepository;
    private final KafkaPathEventService pathEventService;

    public List<PathResponseDto> readAllPaths() {
        Map<UUID, List<PathStation>> pathToStationsMap = pathStationRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        elem -> elem.getPath().getId(),
                        Collectors.toList()
                ));

        return pathRepository.findAll().stream().map(
                      elem -> new PathResponseDto(elem, pathToStationsMap.get(elem.getId()))
                ).toList();
    }

    public PathResponseDto readPathById(UUID id) {
        Path path = pathRepository.findById(id).orElseThrow(() ->
                new NoDataException("No path with id: " + id + "!"));

        return new PathResponseDto(
                path,
                pathStationRepository.findByPathId(id)
        );
    }

    public PathResponseDto readPathByNumberAndCity(String number, String city) {
        Optional<Path> path = pathRepository.findByNumberAndCity(
                number, city
        );

        if (path.isEmpty()) {
            throw new NoDataException("No path with number \"" + number
                    + "\" and city \"" + city + "\"!");
        }

        return new PathResponseDto(
                path.get(),
                pathStationRepository.findByPathId(path.get().getId())
        );
    }

    public List<PathPreviewResponseDto> getPathsListByStationInfo(String name, String address) {
        Optional<Station> station = stationRepository.findByNameAndAddress(
                name, address
        );

        if (station.isEmpty()) {
            throw new NoDataException("No station with name \"" + name
                    + "\" and address \"" + address + "\"!");
        }

        UUID stationId = station.get().getId();
        return pathStationRepository.findByStationId(stationId).stream()
                .map(PathStation::getPath)
                .map(PathPreviewResponseDto::new)
                .toList();
    }

    @Transactional
    public PathResponseDto addPath(PathRequestDto pathDto) {
        try {
            pathRepository.rejectSoftDeleteByNumberAndCity(pathDto.getNumber(), pathDto.getCity());

            Path path = createPathOrFindAndUpdate(pathDto);
            List<PathStation> stations = updateStationsInfo(path, pathDto);

            return new PathResponseDto(
                    path,
                    stations
            );
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    @Transactional
    public PathResponseDto updatePath(UUID id, PathRequestDto pathDto) {
        try {
            Path path = pathRepository.findById(id).get();

            path.updateEntity(pathDto);

            List<PathStation> stations = updateStationsInfo(path, pathDto);

            return new PathResponseDto(
                    path,
                    stations
            );
        } catch (NoSuchElementException ex) {
            throw new NoDataException("No path with id: " + id + "!");
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    @Transactional
    public void deletePath(UUID pathId) {
        Optional<Path> deletablePath = pathRepository.findById(pathId);

        if (deletablePath.isEmpty()) {
            throw new NoDataException("No path with id: " + pathId + "!");
        }

        for (PathStation pathStation: pathStationRepository.findByPathId(pathId)) {
            pathStationRepository.deleteById(pathStation.getId());
        }

        pathRepository.deleteById(pathId);

        pathEventService.sendPathDeleteEvent(pathId);
    }

    private Path createPathOrFindAndUpdate(PathRequestDto pathDto) {
        Path path = pathRepository.findByNumberAndCity(pathDto.getNumber(), pathDto.getCity())
                .orElse(new Path());
        path.setNumber(path.getNumber());
        path.setCity(path.getCity());
        path.setDistance(path.getDistance());

        pathRepository.save(path);
        return path;
    }

    private List<PathStation> updateStationsInfo(Path path, PathRequestDto pathDto) {
        if (pathDto.getStations() == null || pathDto.getTimeFromStart() == null) {
            return pathStationRepository.findByPathId(path.getId());
        }

        if (pathDto.getStations().size() != pathDto.getTimeFromStart().size()) {
            throw new IncorrectBodyException("Stations list's size is not equal to time list's size!");
        }

        List<PathStation> result = new ArrayList<>();

        for (int i = 0; i < pathDto.getStations().size(); i++) {
            pathStationRepository.rejectSoftDeleteByPathIdAndStationId(
                    path.getId(), pathDto.getStations().get(i)
            );

            result.add(updatePathStationInfo(
                    path,
                    pathDto.getStations().get(i),
                    pathDto.getTimeFromStart().get(i)
            ));
        }

        checkAndDeleteExtraConnections(
                path.getId(),
                result.stream()
                        .map(PathStation::getStation)
                        .map(Station::getId)
                        .toList()
        );

        return result;
    }

    private PathStation updatePathStationInfo(Path path, UUID stationId, Long timeFromStart) {
        Optional<PathStation> optionalPathStation = pathStationRepository
                .findByPathIdAndStationId(path.getId(), stationId);

        PathStation pathStation = null;

        if (optionalPathStation.isPresent()) {
            pathStation = optionalPathStation.get();
            pathStation.setTimeSpentFromStart(timeFromStart);
        } else {
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new NoDataException("No station with id: " + stationId + "!"));

            pathStation = new PathStation(path, station);
            pathStation.setTimeSpentFromStart(timeFromStart);
        }

        pathStationRepository.save(pathStation);
        return pathStation;
    }

    private void checkAndDeleteExtraConnections(UUID pathId, List<UUID> correctStationsList) {
        List<PathStation> pathStations = pathStationRepository.findByPathId(pathId);

        for (PathStation pathStation: pathStations) {
            if (!correctStationsList.contains(pathStation.getStation().getId())) {
                pathStationRepository.delete(pathStation);
            }
        }
    }
}