package app.models.dto.departments;

import app.models.Department;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DepartmentResponseDto {

    private UUID id;

    private String name;

    private String address;

    public DepartmentResponseDto(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.address = department.getAddress();
    }
}