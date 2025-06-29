package app.dto.buses;

import app.dto.departments.DepartmentResponseDto;
import app.dto.paths.PathResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusResponseWithPathDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    private String number;

    private PathResponseDto path;

    private DepartmentResponseDto department;

    private Integer seatsNumber;

    private String type;

    private String status;

    public BusResponseWithPathDto(BusResponseDto busDto, PathResponseDto pathDto) {
        this.id = busDto.getId();
        this.number = busDto.getNumber();
        this.path = pathDto;
        this.department = busDto.getDepartment();
        this.seatsNumber = busDto.getSeatsNumber();
        this.type = busDto.getType();
        this.status = busDto.getStatus();
    }
}