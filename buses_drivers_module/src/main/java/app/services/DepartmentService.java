package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Bus;
import app.models.Department;
import app.models.dto.departments.DepartmentRequestDto;
import app.models.dto.departments.DepartmentResponseDto;
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

    public List<DepartmentResponseDto> readAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(DepartmentResponseDto::new)
                .toList();
    }

    public DepartmentResponseDto addDepartment(DepartmentRequestDto departmentDto) {
        try {
            departmentRepository.rejectSoftDelete(departmentDto.getName(),
                    departmentDto.getAddress());
            Optional<Department> optionalDepartment = departmentRepository.findByNameAndAddress(
                    departmentDto.getName(),
                    departmentDto.getAddress()
            );

            if (optionalDepartment.isPresent()) {
                return new DepartmentResponseDto(optionalDepartment.get());
            }

            Department department = new Department(
                    null, departmentDto.getName(),
                    departmentDto.getAddress(), false
            );

            return new DepartmentResponseDto(departmentRepository.saveAndFlush(department));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public DepartmentResponseDto updateDepartment(UUID id, DepartmentRequestDto departmentDto) {
        try {
            Department updatableDepartment = departmentRepository.findById(id).get();

            updatableDepartment.updateEntity(departmentDto);

            return new DepartmentResponseDto(departmentRepository.saveAndFlush(updatableDepartment));
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