package app.controllers;

import app.models.dto.paths.PathRequestDto;
import app.models.dto.paths.PathReadDto;
import app.models.dto.paths.PathResponseDto;
import app.services.PathService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/paths")
@RequiredArgsConstructor
public class PathController {

    private final PathService service;

    @GetMapping
    public List<PathResponseDto> getAllPaths() {
        return service.readAllPaths();
    }

    @GetMapping("/path")
    public PathResponseDto getPath(@RequestBody PathReadDto pathDto) {
        return service.readPathByNumberAndCity(pathDto);
    }

    @PostMapping
    public PathResponseDto addPath(@RequestBody PathRequestDto pathDto) {
        return service.addPath(pathDto);
    }

    @PutMapping
    public PathResponseDto updatePath(@RequestParam("path_id") UUID id, @RequestBody PathRequestDto pathDto) {
        return service.updatePath(id, pathDto);
    }

    @DeleteMapping
    public void deletePath(@RequestParam("path_id")UUID pathId) {
        service.deletePath(pathId);
    }
}