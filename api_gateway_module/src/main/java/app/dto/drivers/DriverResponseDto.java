package app.dto.drivers;

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
public class DriverResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    private String fullName;

    private Integer age;

    private String phone;

    private String email;

    private String licenseNumber;

    private String status;
}