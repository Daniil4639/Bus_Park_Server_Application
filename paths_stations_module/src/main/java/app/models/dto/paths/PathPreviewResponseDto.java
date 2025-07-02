package app.models.dto.paths;

import app.models.Path;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
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