package app;

import app.exceptions.NoDataException;
import app.kafka.KafkaPathEventService;
import app.models.Path;
import app.models.PathStation;
import app.models.Station;
import app.models.dto.paths.PathPreviewResponseDto;
import app.models.dto.paths.PathResponseDto;
import app.repositories.PathRepository;
import app.repositories.PathStationRepository;
import app.repositories.StationRepository;
import app.services.PathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

    @Mock
    private  KafkaPathEventService pathEventService;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private PathRepository pathRepository;

    @Mock
    private PathStationRepository pathStationRepository;

    @InjectMocks
    private PathService service;

    @Test
    @DisplayName("Paths - Service - Read Test - Correct")
    public void pathReadTestCorrect() {
        Station station1 = getStationForTest("name_1", "address_1");
        Station station2 = getStationForTest("name_2", "address_2");
        Path path = getPathForTest("number_1", "city_1");

        List<PathStation> list = List.of(getPathStationForTest(path, station1),
                getPathStationForTest(path, station2));

        Mockito.when(pathStationRepository.findAll()).thenReturn(list);
        Mockito.when(pathRepository.findAll()).thenReturn(List.of(path));

        assertThat(service.readAllPaths(), equalTo(List.of(
                new PathResponseDto(path, list)
        )));
    }

    @Test
    @DisplayName("Paths - Service - Read by id Test - Correct")
    public void pathReadByIdTestCorrect() {
        Station station1 = getStationForTest("name_1", "address_1");
        Station station2 = getStationForTest("name_2", "address_2");
        Path path = getPathForTest("number_1", "city_1");

        List<PathStation> list = List.of(getPathStationForTest(path, station1),
                getPathStationForTest(path, station2));

        Mockito.when(pathRepository.findById(path.getId())).thenReturn(Optional.of(path));
        Mockito.when(pathStationRepository.findByPathId(path.getId())).thenReturn(list);

        assertThat(service.readPathById(path.getId()), equalTo(new PathResponseDto(
                path,
                list
        )));
    }

    @Test
    @DisplayName("Paths - Service - Read by id Test - No data")
    public void pathReadByIdTestNoData() {
        UUID testId = UUID.randomUUID();

        Mockito.when(pathRepository.findById(testId)).thenReturn(Optional.empty());

        try {
            service.readPathById(testId);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Paths - Service - Read by number and city - Correct")
    public void pathReadByNumberAndCityTestCorrect() {
        Station station1 = getStationForTest("name_1", "address_1");
        Station station2 = getStationForTest("name_2", "address_2");
        Path path = getPathForTest("number_2", "city_2");

        List<PathStation> list = List.of(getPathStationForTest(path, station1),
                getPathStationForTest(path, station2));

        Mockito.when(pathRepository.findByNumberAndCity(path.getNumber(), path.getCity()))
                .thenReturn(Optional.of(path));
        Mockito.when(pathStationRepository.findByPathId(path.getId())).thenReturn(list);

        assertThat(service.readPathByNumberAndCity(path.getNumber(), path.getCity()), equalTo(new PathResponseDto(
                path,
                list
        )));
    }

    @Test
    @DisplayName("Paths - Service - Read by number and city - No data")
    public void pathReadByNumberAndCityTestNoData() {
        String number = "number";
        String city = "city";

        Mockito.when(pathRepository.findByNumberAndCity(number, city))
                .thenReturn(Optional.empty());

        try {
            service.readPathByNumberAndCity(number, city);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Paths - Service - Read by station info - Correct")
    public void pathReadByStationInfoTestCorrect() {
        Station station1 = getStationForTest("name_1", "address_1");
        Path path = getPathForTest("number_1", "city_1");

        List<PathStation> list = List.of(getPathStationForTest(path, station1));

        Mockito.when(stationRepository.findByNameAndAddress(station1.getName(),
                station1.getAddress())).thenReturn(Optional.of(station1));

        Mockito.when(pathStationRepository.findByStationId(station1.getId()))
                .thenReturn(list);

        assertThat(service.getPathsListByStationInfo(station1.getName(),
                station1.getAddress()), equalTo(List.of(new PathPreviewResponseDto(path))));
    }

    @Test
    @DisplayName("Paths - Service - Delete test - Correct")
    public void pathsDeleteTestCorrect() {
        Path path = getPathForTest("number_1", "city_1");

        List<PathStation> pathStationList = new ArrayList<>(List.of(getPathStationForTest(null, null),
                getPathStationForTest(null, null), getPathStationForTest(null, null)));

        Mockito.when(pathRepository.findById(path.getId())).thenReturn(Optional.of(path));
        Mockito.when(pathStationRepository.findByPathId(path.getId())).thenReturn(pathStationList);
        for (PathStation pathStation: pathStationList) {
            Mockito.doNothing().when(pathStationRepository).deleteById(pathStation.getId());
        }
        Mockito.doNothing().when(pathEventService).sendPathDeleteEvent(path.getId());

        service.deletePath(path.getId());
    }

    @Test
    @DisplayName("Paths - Service - Delete test - No data")
    public void pathsDeleteTestNoData() {
        UUID id = UUID.randomUUID();

        Mockito.when(pathRepository.findById(id)).thenReturn(Optional.empty());

        try {
            service.deletePath(id);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    private Path getPathForTest(String number, String city) {
        return new Path(
                UUID.randomUUID(),
                number,
                city,
                22.,
                false
        );
    }

    private Station getStationForTest(String name, String address) {
        return new Station(
                UUID.randomUUID(),
                name,
                address,
                false);
    }

    private PathStation getPathStationForTest(Path path, Station station) {
        return new PathStation(
                UUID.randomUUID(),
                path,
                station,
                101010L,
                false
        );
    }
}