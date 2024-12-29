package org.example.carsharingapi.service;

import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarService {

    CarDto save(CreateRequestCarDto createRequestCarDto);

    CarDto getById(Long id);

    Page<CarDto> getAll(Pageable pageable);

    void delete(Long id);

    CarDto update(Long id, UpdateRequestCarDto updateRequestCarDto);
}
