package app.dto.working_logs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkingLogResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    private String licenseNumber;

    private String phone;

    private String email;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}