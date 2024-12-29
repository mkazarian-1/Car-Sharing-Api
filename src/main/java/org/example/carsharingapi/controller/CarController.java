package org.example.carsharingapi.controller;

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

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping()
    public Page<CarDto> getAllCar(Pageable pageable) {
        return carService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto addCar(@Valid @RequestBody CreateRequestCarDto requestCarDto) {
        return carService.save(requestCarDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public CarDto updateCar(@PathVariable Long id,
                            @Valid @RequestBody UpdateRequestCarDto updateRequestCarDto) {
        return carService.update(id, updateRequestCarDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.delete(id);
    }
}
