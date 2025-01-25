package org.example.carsharingapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.example.carsharingapi.mapper.CarMapper;
import org.example.carsharingapi.mapper.impl.CarMapperImpl;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.enums.CarType;
import org.example.carsharingapi.repository.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Spy
    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    @DisplayName("Should save a car and return the corresponding DTO")
    void testSave() {
        // given
        CreateRequestCarDto createRequestCarDto = new CreateRequestCarDto();
        createRequestCarDto.setBrand("Tesla");
        createRequestCarDto.setModel("Model S");
        createRequestCarDto.setType(CarType.HATCHBACK);
        createRequestCarDto.setInventory(10);
        createRequestCarDto.setDailyFee(BigDecimal.valueOf(100));

        Car savedCar = new Car();
        savedCar.setId(1L);
        savedCar.setBrand("Tesla");
        savedCar.setModel("Model S");
        savedCar.setType(CarType.HATCHBACK);
        savedCar.setInventory(10);
        savedCar.setDailyFee(BigDecimal.valueOf(100));

        CarDto expected = new CarDto();
        expected.setId(1L);
        expected.setBrand("Tesla");
        expected.setModel("Model S");
        expected.setType(CarType.HATCHBACK);
        expected.setInventory(10);
        expected.setDailyFee(BigDecimal.valueOf(100));

        when(carRepository.save(any(Car.class))).thenReturn(savedCar);

        // when
        CarDto actual = carService.save(createRequestCarDto);

        // then
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Should retrieve a car by ID and return the corresponding DTO")
    void testGetById_Found() {
        // given
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setModel("Model S");
        car.setType(CarType.HATCHBACK);
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(100));

        CarDto expected = new CarDto();
        expected.setId(1L);
        expected.setBrand("Tesla");
        expected.setModel("Model S");
        expected.setType(CarType.HATCHBACK);
        expected.setInventory(10);
        expected.setDailyFee(BigDecimal.valueOf(100));

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        // when
        CarDto actual = carService.getById(1L);

        // then
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when car ID is not found")
    void testGetById_NotFound() {
        // given
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.getById(1L));

        // then
        assertEquals("Can't find car with current id: 1", exception.getMessage());
        verify(carRepository).findById(1L);
    }

    @Test
    @DisplayName("Should retrieve all cars and return a paginated list of DTOs")
    void testGetAll() {
        // given
        Car car1 = new Car();
        car1.setId(1L);
        car1.setBrand("Tesla");
        car1.setModel("Model S");
        car1.setType(CarType.HATCHBACK);
        car1.setInventory(10);
        car1.setDailyFee(BigDecimal.valueOf(100));

        Car car2 = new Car();
        car2.setId(2L);
        car2.setBrand("Ford");
        car2.setModel("Mustang");
        car1.setType(CarType.SEDAN);
        car1.setInventory(3);
        car1.setDailyFee(BigDecimal.valueOf(100));

        Page<Car> carPage = new PageImpl<>(List.of(car1, car2));
        when(carRepository.findAll(any(Pageable.class))).thenReturn(carPage);

        // when
        Page<CarDto> result = carService.getAll(PageRequest.of(0, 10));

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals("Tesla", result.getContent().get(0).getBrand());
        assertEquals("Ford", result.getContent().get(1).getBrand());
        verify(carRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should delete a car by ID")
    void testDelete() {
        // given
        Long carId = 1L;

        // when
        carService.delete(carId);

        // then
        verify(carRepository).deleteById(carId);
    }

    @Test
    @DisplayName("Should update an existing car and return the updated DTO")
    void testUpdate_Found() {
        // given
        UpdateRequestCarDto updateRequestCarDto = new UpdateRequestCarDto();
        updateRequestCarDto.setInventory(10);
        updateRequestCarDto.setDailyFee(BigDecimal.valueOf(100));

        Car existingCar = new Car();
        existingCar.setId(1L);
        existingCar.setBrand("BMW");
        existingCar.setModel("X5");
        existingCar.setType(CarType.SEDAN);
        existingCar.setInventory(4);
        existingCar.setDailyFee(BigDecimal.valueOf(10));

        Car updateCar = new Car();
        updateCar.setId(1L);
        updateCar.setBrand("BMW");
        updateCar.setModel("X5");
        updateCar.setInventory(10);
        updateCar.setDailyFee(BigDecimal.valueOf(100));

        CarDto expected = new CarDto();
        expected.setId(1L);
        expected.setBrand("BMW");
        expected.setModel("X5");
        expected.setInventory(10);
        expected.setDailyFee(BigDecimal.valueOf(100));

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(updateCar);

        // when
        CarDto actual = carService.update(1L, updateRequestCarDto);

        // then
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
        verify(carRepository).findById(1L);
        verify(carRepository).save(existingCar);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent car")
    void testUpdate_NotFound() {
        // given
        UpdateRequestCarDto updateRequestCarDto = new UpdateRequestCarDto();
        updateRequestCarDto.setInventory(10);
        updateRequestCarDto.setDailyFee(BigDecimal.valueOf(100));

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.update(1L, updateRequestCarDto));

        // then
        assertEquals("Can't find car with current id: 1", exception.getMessage());
        verify(carRepository).findById(1L);
    }
}
