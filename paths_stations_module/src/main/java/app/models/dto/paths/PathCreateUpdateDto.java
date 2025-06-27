package app.models.dto.paths;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PathCreateUpdateDto {

    private UUID id;

    private String number;

    private String city;

    private List<UUID> stations;

    private List<Long> timeFromStart;

    private Double distance;
}