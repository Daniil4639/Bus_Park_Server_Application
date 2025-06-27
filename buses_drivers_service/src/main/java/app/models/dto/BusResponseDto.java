package app.models.dto;

import app.models.Bus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusResponseDto {

    private UUID id;

    private String number;

    private UUID pathId;

    private DepartmentDto department;

    private Integer seatsNumber;

    private String type;

    private String status;

    public BusResponseDto(Bus bus) {
        this.id = bus.getId();
        this.number = bus.getNumber();
        this.pathId = bus.getPathId();
        this.department = new DepartmentDto(bus.getDepartment());
        this.seatsNumber = bus.getSeatsNumber();
        this.type = bus.getType();
        this.status = bus.getStatus();
    }
}