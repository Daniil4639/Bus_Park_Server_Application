package app.controllers;

import app.models.dto.BusResponseDto;
import app.models.dto.DepartmentDto;
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
    public List<DepartmentDto> getAllDepartments() {
        return service.readAllDepartments();
    }

    @GetMapping("/buses_by_department")
    public List<BusResponseDto> getAllBusesByDepartment(@RequestBody DepartmentDto departmentDto) {
        return service.readAllBusesByDepartment(departmentDto);
    }

    @PostMapping
    public DepartmentDto addDepartment(@RequestBody DepartmentDto departmentDto) {
        return service.addDepartment(departmentDto);
    }

    @PutMapping
    public DepartmentDto updateDepartment(@RequestBody DepartmentDto departmentDto) {
        return service.updateDepartment(departmentDto);
    }

    @DeleteMapping
    public void deleteDepartment(@RequestParam("department_id") UUID id) {
        service.deleteDepartment(id);
    }
}