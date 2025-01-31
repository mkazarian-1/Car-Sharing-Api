package org.example.carsharingapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.example.carsharingapi.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car Management", description = "Endpoints for managing cars in the system")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(summary = "Retrieve all cars",
            description = """
                    Retrieves a paginated list of all cars available in the system.
                    
                    Necessary role: None""")
    @GetMapping()
    public Page<CarDto> getAllCar(Pageable pageable) {
        return carService.getAll(pageable);
    }

    @Operation(summary = "Retrieve car details by ID",
            description = """
                    Retrieves detailed information about
                    a specific car using its unique identifier.
                    
                    Necessary role: None""")
    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @Operation(summary = "Add a new car",
            description = """
                    Adds a new car to the system.
                    
                    Necessary role: MANAGER""")
    @PostMapping()
    @PreAuthorize("hasAuthority('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto addCar(@Valid @RequestBody CreateRequestCarDto requestCarDto) {
        return carService.save(requestCarDto);
    }

    @Operation(summary = "Update car details",
            description = """
                    Updates the details of an existing car in the system.
                    
                    Necessary role: MANAGER""")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public CarDto updateCar(@PathVariable Long id,
                            @Valid @RequestBody UpdateRequestCarDto updateRequestCarDto) {
        return carService.update(id, updateRequestCarDto);
    }

    @Operation(summary = "Delete a car",
            description = """
                    Deletes an existing car from the system using its unique identifier.
                    
                    Necessary role: MANAGER""")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.delete(id);
    }
}

