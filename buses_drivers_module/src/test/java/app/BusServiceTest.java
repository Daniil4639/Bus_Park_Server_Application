package app;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Bus;
import app.models.Department;
import app.models.dto.buses.BusRequestDto;
import app.models.dto.buses.BusResponseDto;
import app.repositories.BusRepository;
import app.repositories.DepartmentRepository;
import app.services.BusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private BusService service;

    @Test
    @DisplayName("Buses - Service - Read test - Correct")
    public void busesReadTestCorrect() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);

        Mockito.when(busRepository.findAll()).thenReturn(List.of(bus));

        assertThat(service.readAllBuses(), equalTo(
                List.of(new BusResponseDto(bus))
        ));
    }

    @Test
    @DisplayName("Buses - Service - Read by number - Correct")
    public void busesReadByNumberCorrect() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);

        Mockito.when(busRepository.findByNumber("number")).thenReturn(Optional.of(bus));

        assertThat(service.readBusByNumber("number"), equalTo(new BusResponseDto(bus)));
    }

    @Test
    @DisplayName("Buses - Service - Read by number - No data")
    public void busesReadByNumberNoData() {
        String number = "number";

        Mockito.when(busRepository.findByNumber(number)).thenReturn(Optional.empty());

        try {
            service.readBusByNumber(number);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Buses - Service - Read by department - Correct")
    public void busesReadByDepartmentCorrect() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);

        Mockito.when(departmentRepository.findByNameAndAddress("name", "address"))
                .thenReturn(Optional.of(department));
        Mockito.when(busRepository.findAllByDepartmentId(department.getId())).thenReturn(List.of(bus));

        assertThat(service.readAllBusesByDepartment("name", "address"), equalTo(
                List.of(new BusResponseDto(bus))
        ));
    }

    @Test
    @DisplayName("Buses - Service - Read by department - No data")
    public void busesReadByDepartmentNoData() {
        String name = "name";
        String address = "address";

        Mockito.when(departmentRepository.findByNameAndAddress(name, address)).thenReturn(Optional.empty());

        try {
            service.readAllBusesByDepartment(name, address);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Buses - Service - Add test - Correct")
    public void busAddTestCorrect() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus busResponse = getBusForTest("number", department);
        Bus busRequest = getBusForTest("number", department);
        busRequest.setId(null);
        busRequest.setPathId(busResponse.getPathId());

        Mockito.doNothing().when(busRepository).rejectSoftDelete("number");
        Mockito.when(busRepository.findByNumber("number")).thenReturn(Optional.empty());
        Mockito.when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        Mockito.when(busRepository.saveAndFlush(busRequest)).thenReturn(busResponse);

        assertThat(service.addBus(new BusRequestDto(
                "number",
                busRequest.getPathId(),
                department.getId(),
                14,
                "type",
                "status"
        )), equalTo(new BusResponseDto(busResponse)));
    }

    @Test
    @DisplayName("Buses - Service - Add test - Incorrect body")
    public void busAddTestIncorrectBody() {
        String number = "number";

        Mockito.doThrow(new IncorrectBodyException("")).when(busRepository).rejectSoftDelete(number);

        try {
            service.addBus(new BusRequestDto(
                    "number",
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    14,
                    "type",
                    "status"));

            assertThat(1, equalTo(2));
        } catch (IncorrectBodyException ignored) {}
    }

    @Test
    @DisplayName("Buses - Service - Add test - No data")
    public void busAddTestNoData() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);

        Mockito.doNothing().when(busRepository).rejectSoftDelete("number");
        Mockito.when(busRepository.findByNumber("number")).thenReturn(Optional.empty());
        Mockito.when(departmentRepository.findById(department.getId())).thenReturn(Optional.empty());

        try {
            service.addBus(new BusRequestDto(
                    "number",
                    UUID.randomUUID(),
                    department.getId(),
                    14,
                    "type",
                    "status"));

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Buses - Service - Update test - Correct")
    public void busUpdateTestCorrect() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);
        Bus expectedBus = getBusForTest("number_new", department);
        expectedBus.setId(bus.getId());
        expectedBus.setPathId(bus.getPathId());

        Mockito.when(busRepository.findById(bus.getId())).thenReturn(Optional.of(bus));
        Mockito.when(busRepository.saveAndFlush(expectedBus)).thenReturn(expectedBus);

        assertThat(service.updateBus(bus.getId(), new BusRequestDto("number_new", null, null, null, null, null)),
                equalTo(new BusResponseDto(expectedBus)));
    }

    @Test
    @DisplayName("Buses - Service - Update test - No data")
    public void busUpdateTestNoData() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);
        Bus expectedBus = getBusForTest("number_new", department);
        expectedBus.setId(bus.getId());
        expectedBus.setPathId(bus.getPathId());

        Mockito.when(busRepository.findById(bus.getId())).thenReturn(Optional.of(bus));
        Mockito.when(departmentRepository.findById(department.getId())).thenReturn(Optional.empty());

        try {
            service.updateBus(bus.getId(), new BusRequestDto("number_new", null, department.getId(), null, null, null));

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Buses - Service - Delete test - Correct")
    public void busesDeleteTestCorrect() {
        Department department = new Department(UUID.randomUUID(), "name", "address", false);
        Bus bus = getBusForTest("number", department);

        Mockito.when(busRepository.findById(bus.getId())).thenReturn(Optional.of(bus));

        service.deleteBus(bus.getId());
    }

    @Test
    @DisplayName("Buses - Service - Delete test - No data")
    public void busesDeleteTestNoData() {
        UUID id = UUID.randomUUID();

        Mockito.when(busRepository.findById(id)).thenReturn(Optional.empty());

        try {
            service.deleteBus(id);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    private Bus getBusForTest(String number, Department department) {
        return new Bus(
                UUID.randomUUID(),
                number,
                UUID.randomUUID(),
                department,
                14,
                "type",
                "status",
                false);
    }
}