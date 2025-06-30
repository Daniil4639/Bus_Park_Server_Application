package app.services;

import app.dto.buses.BusResponseWithPathDto;
import app.dto.departments.DepartmentResponseDto;
import app.dto.drivers.DriverResponseDto;
import app.dto.paths.PathPreviewResponseDto;
import app.dto.paths.PathResponseDto;
import app.dto.stations.StationResponseDto;
import app.dto.working_logs.WorkingLogResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserRoleValidationService {

    public BusResponseWithPathDto clearBus(BusResponseWithPathDto busDto) {
        if (isNotAdmin()) {
            busDto.setId(null);
            clearDepartment(busDto.getDepartment());

            if (busDto.getPath() != null) {
                clearPath(busDto.getPath());
            }
        }

        return busDto;
    }

    public DepartmentResponseDto clearDepartment(DepartmentResponseDto departmentDto) {
        if (isNotAdmin()) {
            departmentDto.setId(null);
        }

        return departmentDto;
    }

    public DriverResponseDto clearDriver(DriverResponseDto driverDto) {
        if (isNotAdmin()) {
            driverDto.setId(null);
        }

        return driverDto;
    }

    public PathPreviewResponseDto clearPathPreview(PathPreviewResponseDto pathPreviewDto) {
        if (isNotAdmin()) {
            pathPreviewDto.setId(null);
        }

        return pathPreviewDto;
    }

    public PathResponseDto clearPath(PathResponseDto pathDto) {
        if (isNotAdmin()) {
            pathDto.setId(null);
            clearStation(pathDto.getBeginStation());
            clearStation(pathDto.getEndStation());
            for (StationResponseDto stationDto: pathDto.getStations()) {
                clearStation(stationDto);
            }
        }

        return pathDto;
    }

    public StationResponseDto clearStation(StationResponseDto stationDto) {
        if (isNotAdmin()) {
            stationDto.setId(null);
        }

        return stationDto;
    }

    public WorkingLogResponseDto clearWorkingLog(WorkingLogResponseDto logDto) {
        if (isNotAdmin()) {
            logDto.setId(null);
        }

        return logDto;
    }

    private boolean isNotAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return true;
        }

        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .noneMatch(auth ->
                        auth.getAuthority().equals("ROLE_ADMIN"));
    }
}