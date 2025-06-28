package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Bus;
import app.models.Department;
import app.models.dto.BusResponseDto;
import app.models.dto.DepartmentDto;
import app.repositories.BusRepository;
import app.repositories.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final BusRepository busRepository;

    public List<DepartmentDto> readAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentDto::new)
                .toList();
    }

    public List<BusResponseDto> readAllBusesByDepartment(DepartmentDto departmentDto) {
        Department department = departmentRepository.findByNameAndAddress(
                departmentDto.getName(), departmentDto.getAddress())
                .orElseThrow(() -> new NoDataException("No department with name \"" + departmentDto.getName()
                        + "\" and address \"" + departmentDto.getAddress() + "\"!"
                ));

        return busRepository.findAllByDepartmentId(department.getId()).stream()
                .map(BusResponseDto::new)
                .toList();
    }

    public DepartmentDto addDepartment(DepartmentDto departmentDto) {
        try {
            departmentRepository.rejectSoftDelete(departmentDto.getName(),
                    departmentDto.getAddress());
            Optional<Department> optionalDepartment = departmentRepository.findByNameAndAddress(
                    departmentDto.getName(),
                    departmentDto.getAddress()
            );

            if (optionalDepartment.isPresent()) {
                return new DepartmentDto(optionalDepartment.get());
            }

            Department department = new Department(
                    null, departmentDto.getName(),
                    departmentDto.getAddress(), false
            );

            return new DepartmentDto(departmentRepository.saveAndFlush(department));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public DepartmentDto updateDepartment(DepartmentDto departmentDto) {
        UUID id = departmentDto.getId();
        if (id == null) {
            throw new IncorrectBodyException("No department id!");
        }

        try {
            Department updatableDepartment = departmentRepository.findById(id).get();

            updatableDepartment.updateEntity(departmentDto);

            return new DepartmentDto(departmentRepository.saveAndFlush(updatableDepartment));
        } catch (NoSuchElementException ex) {
            throw new NoDataException("No department with id: " + id + "!");
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteDepartment(UUID id) {
        Optional<Department> deletableDepartment = departmentRepository.findById(id);

        if (deletableDepartment.isEmpty()) {
            throw new NoDataException("No department with id: " + id + "!");
        }
        Department department = deletableDepartment.get();

        for (Bus bus: busRepository.findAllByDepartmentId(id)) {
            busRepository.deleteById(bus.getId());
        }

        departmentRepository.deleteById(id);
    }
}