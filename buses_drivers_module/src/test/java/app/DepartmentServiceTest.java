package app;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Bus;
import app.models.Department;
import app.models.dto.departments.DepartmentRequestDto;
import app.models.dto.departments.DepartmentResponseDto;
import app.repositories.BusRepository;
import app.repositories.DepartmentRepository;
import app.services.DepartmentService;
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
public class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private DepartmentService service;

    @Test
    @DisplayName("Departments - Service - Read test - Correct")
    public void departmentReadTestCorrect() {
        Department department1 = getDepartmentForTest("name_1", "address_1");
        Department department2 = getDepartmentForTest("name_2", "address_2");

        List<Department> departments = List.of(department1, department2);

        Mockito.when(departmentRepository.findAll()).thenReturn(departments);

        assertThat(service.readAllDepartments(), equalTo(departments.stream()
                .map(DepartmentResponseDto::new).toList()));
    }

    @Test
    @DisplayName("Departments - Service - Add test - Correct")
    public void departmentAddTestCorrect() {
        String name = "name";
        String address = "address";

        Department departmentRequest = new Department(null, name, address, false);
        Department departmentResponse = getDepartmentForTest(name, address);

        Mockito.doNothing().when(departmentRepository).rejectSoftDelete(name, address);
        Mockito.when(departmentRepository.findByNameAndAddress(name, address)).thenReturn(Optional.empty());
        Mockito.when(departmentRepository.saveAndFlush(departmentRequest)).thenReturn(departmentResponse);

        assertThat(service.addDepartment(new DepartmentRequestDto(name, address)),
                equalTo(new DepartmentResponseDto(departmentResponse)));
    }

    @Test
    @DisplayName("Departments - Service - Add test - Incorrect body")
    public void departmentAddTestIncorrectBody() {
        Mockito.doThrow(new IncorrectBodyException("")).when(departmentRepository)
                .rejectSoftDelete("name", null);

        try {
            service.addDepartment(new DepartmentRequestDto("name", null));

            assertThat(1, equalTo(2));
        } catch (IncorrectBodyException ignored) {}
    }

    @Test
    @DisplayName("Departments - Service - Update test - Correct")
    public void departmentUpdateTestCorrect() {
        Department department = getDepartmentForTest("name", "address");
        Department expectedDepartment = new Department(department.getId(),
                "name_test", "address", false);

        Mockito.when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        Mockito.when(departmentRepository.saveAndFlush(expectedDepartment)).thenReturn(expectedDepartment);

        assertThat(service.updateDepartment(department.getId(), new DepartmentRequestDto(
                "name_test", null
        )), equalTo(new DepartmentResponseDto(expectedDepartment)));
    }

    @Test
    @DisplayName("Departments - Service - Update test - No data")
    public void departmentUpdateTestNoData() {
        UUID id = UUID.randomUUID();

        Mockito.when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        try {
            service.updateDepartment(id, new DepartmentRequestDto(null, null));

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    @Test
    @DisplayName("Departments - Service - Delete test - Correct")
    public void departmentDeleteTestCorrect() {
        Department department = getDepartmentForTest("name", "address");
        Bus bus = new Bus(
                UUID.randomUUID(),
                "number",
                UUID.randomUUID(),
                department,
                14,
                "type",
                "status",
                false);

        Mockito.when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        Mockito.when(busRepository.findAllByDepartmentId(department.getId())).thenReturn(List.of(bus));
        Mockito.doNothing().when(busRepository).deleteById(bus.getId());

        service.deleteDepartment(department.getId());
    }

    @Test
    @DisplayName("Departments - Service - Delete test - No data")
    public void departmentDeleteTestNoData() {
        UUID id = UUID.randomUUID();

        Mockito.when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        try {
            service.deleteDepartment(id);

            assertThat(1, equalTo(2));
        } catch (NoDataException ignored) {}
    }

    private Department getDepartmentForTest(String name, String address) {
        return new Department(
                UUID.randomUUID(),
                name,
                address,
                false
        );
    }
}