package app.models;

import app.models.dto.paths.PathCreateUpdateDto;
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

    public void updateEntity(PathCreateUpdateDto pathDto) {
        this.number = pathDto.getNumber();
        this.city = pathDto.getCity();
        this.distance = pathDto.getDistance();
    }
}