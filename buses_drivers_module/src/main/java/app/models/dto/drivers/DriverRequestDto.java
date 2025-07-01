package app.models.dto.drivers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequestDto {

    private String schedule;

    private String fullName;

    private Integer age;

    private String phone;

    private String email;

    private String licenseNumber;

    private String status;
}