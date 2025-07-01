package app.models;

import app.models.dto.drivers.DriverRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Driver implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String schedule;

    private String fullName;

    private Integer age;

    private String phone;

    private String email;

    private String licenseNumber;

    private String status;

    private Boolean isDeleted;

    public void updateEntity(DriverRequestDto driverDto) {
        if (driverDto.getSchedule() != null) {
            this.schedule = driverDto.getSchedule();
        }

        if (driverDto.getFullName() != null) {
            this.fullName = driverDto.getFullName();
        }

        if (driverDto.getAge() != null) {
            this.age = driverDto.getAge();
        }

        if (driverDto.getPhone() != null) {
            this.phone = driverDto.getPhone();
        }

        if (driverDto.getEmail() != null) {
            this.email = driverDto.getEmail();
        }

        if (driverDto.getLicenseNumber() != null) {
            this.licenseNumber = driverDto.getLicenseNumber();
        }

        if (driverDto.getStatus() != null) {
            this.status = driverDto.getStatus();
        }
    }
}