package app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "paths_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PathStation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "path_id")
    private Path path;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_id")
    private Station station;

    private Long timeSpentFromStart;

    private Boolean isDeleted;

    public PathStation(Path path, Station station) {
        this.path = path;
        this.station = station;
    }
}