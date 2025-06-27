package app.models.dto.stations;

import app.models.Station;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StationDto {

    private UUID id;

    private String name;

    private String address;

    public StationDto(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.address = station.getAddress();
    }
}