package app.models.dto.drivers;

import app.models.Driver;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DriverResponseDto {

    private UUID id;

    private String schedule;

    private String fullName;

    private Integer age;

    private String phone;

    private String email;

    private String licenseNumber;

    private String status;

    public DriverResponseDto(Driver driver) {
        this.id = driver.getId();
        this.schedule = driver.getSchedule();
        this.fullName = driver.getFullName();
        this.age = driver.getAge();
        this.phone = driver.getPhone();
        this.email = driver.getEmail();
        this.licenseNumber = driver.getLicenseNumber();
        this.status = driver.getStatus();
    }
}