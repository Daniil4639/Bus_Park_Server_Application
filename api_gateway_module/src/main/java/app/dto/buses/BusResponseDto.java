package app.dto.buses;

import app.dto.departments.DepartmentResponseDto;
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

    private DepartmentResponseDto department;

    private Integer seatsNumber;

    private String type;

    private String status;
}