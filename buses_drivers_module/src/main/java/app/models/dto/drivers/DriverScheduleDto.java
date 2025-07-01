package app.models.dto.drivers;

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

    public DriverScheduleDto(DriverResponseDto driver) {
        this.licenseNumber = driver.getLicenseNumber();
        this.phone = driver.getPhone();
        this.email = driver.getEmail();
        this.schedule = driver.getSchedule();
    }
}