package app;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.PathStation;
import app.models.Station;
import app.models.dto.stations.StationRequestDto;
import app.models.dto.stations.StationResponseDto;
import app.repositories.PathStationRepository;
import app.repositories.StationRepository;
import app.services.StationService;
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
public class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private PathStationRepository pathStationRepository;

    @InjectMocks
    private StationService service;

    @Test
    @DisplayName("Stations - Service - Read Test - Correct")
    public void stationReadTestCorrect() {
        Station station1 = getStationForTest("name_1", "address_1");
        Station station2 = getStationForTest("name_2", "address_2");

        List<Station> list = List.of(station1, station2);

        Mockito.when(stationRepository.findAll()).thenReturn(list);

        assertThat(service.readAllStations(), equalTo(
                list.stream().map(StationResponseDto::new).toList()));
    }

    @Test
    @DisplayName("Stations - Service - Add test - Correct")
    public void stationAddTestCorrect() {
        Mockito.doNothing().when(stationRepository).rejectSoftDelete("name_1", "address_1");

        Station stationRequest = new Station(null, "name_1", "address_1", false);
        Station stationResponse = getStationForTest("name_1", "address_1");

        Mockito.when(stationRepository.findByNameAndAddress("name_1", "address_1")).thenReturn(
                Optional.empty()
        );

        Mockito.when(stationRepository.saveAndFlush(stationRequest)).thenReturn(stationResponse);

        assertThat(service.addStation(new StationRequestDto("name_1", "address_1")),
                equalTo(new StationResponseDto(stationResponse)));
    }

    @Test
    @DisplayName("Stations - Service - Add test - Incorrect Body")
    public void stationAddTestIncorrectBody() {
        Mockito.doThrow(new IncorrectBodyException("")).when(stationRepository).rejectSoftDelete("name_1", null);

        try {
            service.addStation(new StationRequestDto("name_1", null));

            assertThat(1, equalTo(2));
        } catch (IncorrectBodyException ignored) {}
    }

    @Test
    @DisplayName("Stations - Service - Update test - Correct")
    public void stationUpdateTestCorrect() {
        Station station = getStationForTest("name_1", "address_1");
        Station expectedStation = new Station(station.getId(), "name_1",
                "address_2", false);

        Mockito.when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));

        Mockito.when(stationRepository.saveAndFlush(expectedStation)).thenReturn(expectedStation);

        assertThat(service.updateStationInfo(station.getId(), new StationRequestDto(null, "address_2")),
                equalTo(new StationResponseDto(expectedStation)));
    }

    @Test
    @DisplayName("Stations - Service - Update test - No data")
    public void stationUpdateTestNoData() {
        UUID testId = UUID.randomUUID();

        Mockito.when(stationRepository.findById(testId)).thenReturn(Optional.empty());

        try {
            service.updateStationInfo(testId, new StationRequestDto("name_1", "address_1"));

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Stations - Service - Delete test - Correct")
    public void stationDeleteTestCorrect() {
        Station station = getStationForTest("name_1", "address_1");

        Mockito.when(stationRepository.findById(station.getId())).thenReturn(Optional.of(station));

        List<PathStation> pathStationList = new ArrayList<>(List.of(getPathStationForTest(),
                getPathStationForTest(), getPathStationForTest()));

        Mockito.when(pathStationRepository.findByStationId(station.getId()))
                .thenReturn(pathStationList);

        for (PathStation pathStation: pathStationList) {
            Mockito.doNothing().when(pathStationRepository).deleteById(pathStation.getId());
        }

        service.deleteStationById(station.getId());
    }

    @Test
    @DisplayName("Stations - Service - Delete test - No data")
    public void stationDeleteTestNoData() {
        UUID testId = UUID.randomUUID();

        Mockito.when(stationRepository.findById(testId)).thenReturn(Optional.empty());

        try {
            service.deleteStationById(testId);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    private PathStation getPathStationForTest() {
        return new PathStation(
                UUID.randomUUID(),
                null,
                null,
                1010101L,
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
}