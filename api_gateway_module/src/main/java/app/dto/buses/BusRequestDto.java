package app.dto.buses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusRequestDto {

    private String number;

    private UUID pathId;

    private UUID departmentId;

    private Integer seatsNumber;

    private String type;

    private String status;
}