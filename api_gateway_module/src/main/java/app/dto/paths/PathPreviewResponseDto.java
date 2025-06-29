package app.dto.paths;

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
public class PathPreviewResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    private String number;

    private String city;
}