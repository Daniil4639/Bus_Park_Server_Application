package app.models.dto;

import app.models.Driver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {

    private UUID id;

    private String fullName;

    private Integer age;

    private String phone;

    private String email;

    private String licenseNumber;

    private String status;

    public DriverDto(Driver driver) {
        this.id = driver.getId();
        this.fullName = driver.getFullName();
        this.age = driver.getAge();
        this.phone = driver.getPhone();
        this.email = driver.getEmail();
        this.licenseNumber = driver.getLicenseNumber();
        this.status = driver.getStatus();
    }
}