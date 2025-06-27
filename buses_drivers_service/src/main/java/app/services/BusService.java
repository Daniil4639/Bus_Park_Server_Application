package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Bus;
import app.models.Department;
import app.models.dto.BusCreateUpdateDto;
import app.models.dto.BusResponseDto;
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
public class BusService {

    private final BusRepository busRepository;
    private final DepartmentRepository departmentRepository;

    public List<BusResponseDto> readAllBuses() {
        return busRepository.findAll().stream()
                .map(BusResponseDto::new)
                .toList();
    }

    public BusResponseDto addBus(BusCreateUpdateDto busDto) {
        try {
            busRepository.rejectSoftDelete(busDto.getNumber());
            Optional<Bus> optionalBus = busRepository.findByNumber(busDto.getNumber());

            Bus bus = optionalBus.orElse(new Bus());
            bus.updateEntity(busDto);

            Department department = departmentRepository.findById(busDto.getDepartmentId())
                    .orElseThrow(() ->
                            new NoDataException("No department with id: " + busDto.getDepartmentId() + "!"));
            bus.setDepartment(department);

            return new BusResponseDto(busRepository.saveAndFlush(bus));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public BusResponseDto updateBus(BusCreateUpdateDto busDto) {
        UUID id = busDto.getId();
        if (id == null) {
            throw new IncorrectBodyException("No bus id!");
        }

        try {
            Bus updatableBus = busRepository.findById(id).get();

            updatableBus.updateEntity(busDto);
            if (busDto.getDepartmentId() != null) {
                Department department = departmentRepository.findById(busDto.getDepartmentId())
                        .orElseThrow(() ->
                                new NoDataException("No department with id: " + busDto.getDepartmentId() + "!"));
                updatableBus.setDepartment(department);
            }

            return new BusResponseDto(busRepository.saveAndFlush(updatableBus));
        } catch (NoSuchElementException ex) {
            throw new NoDataException("No bus with id: " + id + "!");
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    @Transactional
    public void deleteBus(UUID id) {
        Optional<Bus> deletableBus = busRepository.findById(id);

        if (deletableBus.isEmpty()) {
            throw new NoDataException("No bus with id: " + id + "!");
        }
        Bus bus = deletableBus.get();

        bus.setIsDeleted(true);
        busRepository.saveAndFlush(bus);
    }
}