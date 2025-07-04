package app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverScheduleDto {

    private String licenseNumber;

    private String phone;

    private String email;

    private String schedule;
}