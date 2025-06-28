package app.models.dto.paths;

import app.models.Path;
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

    private UUID id;

    private String number;

    private String city;

    public PathPreviewResponseDto(Path path) {
        this.id = path.getId();
        this.number = path.getNumber();
        this.city = path.getCity();
    }
}