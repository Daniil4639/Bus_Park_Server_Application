package app.models.dto.buses;

import app.models.Bus;
import app.models.dto.departments.DepartmentResponseDto;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BusResponseDto {

    private UUID id;

    private String number;

    private UUID pathId;

    private DepartmentResponseDto department;

    private Integer seatsNumber;

    private String type;

    private String status;

    public BusResponseDto(Bus bus) {
        this.id = bus.getId();
        this.number = bus.getNumber();
        this.pathId = bus.getPathId();
        this.department = new DepartmentResponseDto(bus.getDepartment());
        this.seatsNumber = bus.getSeatsNumber();
        this.type = bus.getType();
        this.status = bus.getStatus();
    }
}