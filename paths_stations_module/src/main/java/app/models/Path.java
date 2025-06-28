package app.models;

import app.models.dto.paths.PathRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "paths")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Path implements Serializable {

    @Id
    private UUID id;

    private String number;

    private String city;

    private Double distance;

    private Boolean isDeleted;

    public void updateEntity(PathRequestDto pathDto) {
        if (pathDto.getNumber() != null) {
            this.number = pathDto.getNumber();
        }

        if (pathDto.getCity() != null) {
            this.city = pathDto.getCity();
        }

        if (pathDto.getDistance() != null) {
            this.distance = pathDto.getDistance();
        }
    }
}