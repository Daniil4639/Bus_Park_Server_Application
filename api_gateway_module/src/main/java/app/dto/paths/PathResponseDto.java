package app.dto.paths;

import app.dto.stations.StationResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PathResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    private String number;

    private String city;

    private StationResponseDto beginStation;

    private StationResponseDto endStation;

    private List<StationResponseDto> stations;

    private Double distance;
}