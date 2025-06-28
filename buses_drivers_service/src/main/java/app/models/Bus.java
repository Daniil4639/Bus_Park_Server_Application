package app.models;

import app.models.dto.buses.BusRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "buses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Bus implements Serializable {

    @Id
    private UUID id;

    private String number;

    private UUID pathId;

    @OneToOne(fetch = FetchType.EAGER)
    private Department department;

    private Integer seatsNumber;

    private String type;

    private String status;

    private Boolean isDeleted;

    public void updateEntity(BusRequestDto dto) {
        if (dto.getNumber() != null) {
            this.number = dto.getNumber();
        }

        if (dto.getSeatsNumber() != null) {
            this.seatsNumber = dto.getSeatsNumber();
        }

        if (dto.getType() != null) {
            this.type = dto.getType();
        }

        if (dto.getStatus() != null) {
            this.status = dto.getStatus();
        }
    }
}