package app.services;

import app.exceptions.IncorrectBodyException;
import app.exceptions.NoDataException;
import app.models.Bus;
import app.models.Department;
import app.models.dto.buses.BusRequestDto;
import app.models.dto.buses.BusResponseDto;
import app.models.dto.departments.DepartmentRequestDto;
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

    public BusResponseDto readBusByNumber(String number) {
        return new BusResponseDto(busRepository.findByNumber(number).orElseThrow(() ->
                new NoDataException("No bus with number: " + number + "!")
        ));
    }

    public List<BusResponseDto> readAllBusesByDepartment(String name, String address) {
        Department department = departmentRepository.findByNameAndAddress(
                        name, address)
                .orElseThrow(() -> new NoDataException("No department with name \"" + name
                        + "\" and address \"" + address + "\"!"
                ));

        return busRepository.findAllByDepartmentId(department.getId()).stream()
                .map(BusResponseDto::new)
                .toList();
    }

    public BusResponseDto addBus(BusRequestDto busDto) {
        try {
            busRepository.rejectSoftDelete(busDto.getNumber());
            Optional<Bus> optionalBus = busRepository.findByNumber(busDto.getNumber());

            Bus bus = optionalBus.orElse(new Bus());
            bus.setNumber(busDto.getNumber());
            bus.setSeatsNumber(busDto.getSeatsNumber());
            bus.setType(busDto.getType());
            bus.setStatus(busDto.getStatus());

            Department department = departmentRepository.findById(busDto.getDepartmentId())
                    .orElseThrow(() ->
                            new NoDataException("No department with id: " + busDto.getDepartmentId() + "!"));
            bus.setDepartment(department);

            return new BusResponseDto(busRepository.saveAndFlush(bus));
        } catch (Exception ex) {
            throw new IncorrectBodyException(ex.getMessage());
        }
    }

    public BusResponseDto updateBus(UUID id, BusRequestDto busDto) {
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

        busRepository.deleteById(id);
    }
}