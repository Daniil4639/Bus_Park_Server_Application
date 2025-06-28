package app.models;

import app.models.dto.stations.StationRequestDto;
import app.models.dto.stations.StationResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "stations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Station implements Serializable {

    @Id
    private UUID id;

    private String name;

    private String address;

    private Boolean isDeleted;

    public void updateEntity(StationRequestDto stationDto) {
        if (stationDto.getName() != null) {
            this.name = stationDto.getName();
        }
        if (stationDto.getAddress() != null) {
            this.address = stationDto.getAddress();
        }
    }
}