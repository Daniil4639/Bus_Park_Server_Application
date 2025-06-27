package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Path;
import app.models.PathStation;
import app.models.Station;
import app.models.dto.paths.PathCreateUpdateDto;
import app.models.dto.paths.PathReadDto;
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

    public PathResponseDto readPathByNumberAndCity(PathReadDto pathDto) {
        Optional<Path> path = pathRepository.findByNumberAndCity(
                pathDto.getNumber(), pathDto.getCity()
        );

        if (path.isEmpty()) {
            throw new NoDataException("No path with number \"" + pathDto.getNumber()
                    + "\" and city \"" + pathDto.getCity() + "\"!");
        }

        return new PathResponseDto(
                path.get(),
                pathStationRepository.findByPathId(path.get().getId())
        );
    }

    @Transactional
    public PathResponseDto addPath(PathCreateUpdateDto pathDto) {
        try {
            pathRepository.rejectSoftDelete(pathDto.getNumber(), pathDto.getCity());

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
    public PathResponseDto updatePath(PathCreateUpdateDto pathDto) {
        UUID id = pathDto.getId();
        if (id == null) {
            throw new IncorrectBodyException("No path id!");
        }

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
        Path path = deletablePath.get();

        for (PathStation pathStation: pathStationRepository.findByPathId(pathId)) {
            pathStation.setIsDeleted(true);
        }

        path.setIsDeleted(true);
        pathRepository.saveAndFlush(path);
    }

    private Path createPathOrFindAndUpdate(PathCreateUpdateDto pathDto) {
        Path path = pathRepository.findByNumberAndCity(pathDto.getNumber(), pathDto.getCity())
                .orElse(new Path());
        path.setNumber(path.getNumber());
        path.setCity(path.getCity());
        path.setDistance(path.getDistance());

        pathRepository.save(path);
        return path;
    }

    private List<PathStation> updateStationsInfo(Path path, PathCreateUpdateDto pathDto) {
        if (pathDto.getStations() == null || pathDto.getTimeFromStart() == null) {
            return pathStationRepository.findByPathId(path.getId());
        }

        if (pathDto.getStations().size() != pathDto.getTimeFromStart().size()) {
            throw new IncorrectBodyException("Stations list's size is not equal to time list's size!");
        }

        List<PathStation> result = new ArrayList<>();

        for (int i = 0; i < pathDto.getStations().size(); i++) {
            pathStationRepository.rejectSoftDelete(
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