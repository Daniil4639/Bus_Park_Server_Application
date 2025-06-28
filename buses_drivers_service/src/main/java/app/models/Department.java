package app.models;

import app.models.dto.departments.DepartmentRequestDto;
import app.models.dto.departments.DepartmentResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "departments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Department implements Serializable {

    @Id
    private UUID id;

    private String name;

    private String address;

    private Boolean isDeleted;

    public void updateEntity(DepartmentRequestDto departmentDto) {
        if (departmentDto.getName() != null) {
            this.name = departmentDto.getName();
        }

        if (departmentDto.getAddress() != null) {
            this.address = departmentDto.getAddress();
        }
    }
}