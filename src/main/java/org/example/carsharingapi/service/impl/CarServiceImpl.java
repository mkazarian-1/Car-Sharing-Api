package org.example.carsharingapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.example.carsharingapi.mapper.CarMapper;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.repository.CarRepository;
import org.example.carsharingapi.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateRequestCarDto createRequestCarDto) {
        return carMapper.toDto(
                carRepository.save(
                        carMapper.toEntity(createRequestCarDto)));
    }

    @Override
    public CarDto getById(Long id) {
        return carMapper.toDto(
                carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Can't find car with current id: " + id)));
    }

    @Override
    public Page<CarDto> getAll(Pageable pageable) {
        return carRepository.findAll(pageable).map(carMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public CarDto update(Long id, UpdateRequestCarDto updateRequestCarDto) {
        Car car = carRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Can't find car with current id: " + id));
        carMapper.updateCar(updateRequestCarDto,car);
        return carMapper.toDto(carRepository.save(car));
    }
}
