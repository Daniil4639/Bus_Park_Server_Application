package app.models.dto.paths;

import app.models.Path;
import app.models.PathStation;
import app.models.dto.stations.StationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PathResponseDto {

    private UUID id;

    private String number;

    private String city;

    private StationDto beginStation;

    private StationDto endStation;

    private List<StationDto> stations;

    private Double distance;

    public PathResponseDto(Path path, List<PathStation> stations) {
        this.id = path.getId();
        this.number = path.getNumber();
        this.city = path.getCity();
        this.stations = stations.stream()
                .sorted(Comparator.comparing(PathStation::getTimeSpentFromStart))
                .map(PathStation::getStation)
                .map(StationDto::new)
                .toList();
        this.beginStation = this.stations.get(0);
        this.endStation = this.stations.get(this.stations.size() - 1);
        this.distance = path.getDistance();
    }
}