package app.models.dto;

import app.models.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {

    private UUID id;

    private String name;

    private String address;

    public DepartmentDto(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.address = department.getAddress();
    }
}