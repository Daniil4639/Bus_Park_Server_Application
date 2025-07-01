package app.controllers;

import app.models.dto.departments.DepartmentRequestDto;
import app.models.dto.departments.DepartmentResponseDto;
import app.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @GetMapping
    public List<DepartmentResponseDto> getAllDepartments() {
        return service.readAllDepartments();
    }

    @PostMapping
    public DepartmentResponseDto addDepartment(@RequestBody DepartmentRequestDto departmentDto) {
        return service.addDepartment(departmentDto);
    }

    @PutMapping
    public DepartmentResponseDto updateDepartment(@RequestParam("department_id") UUID id,
                                                  @RequestBody DepartmentRequestDto departmentDto) {
        return service.updateDepartment(id, departmentDto);
    }

    @DeleteMapping
    public void deleteDepartment(@RequestParam("department_id") UUID id) {
        service.deleteDepartment(id);
    }
}