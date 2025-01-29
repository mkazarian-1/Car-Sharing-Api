package org.example.carsharingapi.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.car.CreateRequestCarDto;
import org.example.carsharingapi.dto.car.UpdateRequestCarDto;
import org.example.carsharingapi.exeption.ElementNotFoundException;
import org.example.carsharingapi.mapper.CarMapper;
import org.example.carsharingapi.mapper.impl.CarMapperImpl;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.enums.CarType;
import org.example.carsharingapi.repository.CarRepository;
import org.example.carsharingapi.util.CarTestUtil;
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

        when(carRepository.save(any(Car.class))).thenReturn(CarTestUtil.getCar());

        // when
        CarDto actual = carService.save(createRequestCarDto);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(CarTestUtil.getCarDto());
        verify(carRepository).save(any(Car.class));
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Should retrieve a car by ID and return the corresponding DTO")
    void testGetById_Found() {
        // given
        when(carRepository.findById(1L)).thenReturn(Optional.of(CarTestUtil.getCar()));

        // when
        CarDto actual = carService.getById(1L);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(CarTestUtil.getCarDto());
        verify(carRepository).findById(1L);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Should throw ElementNotFoundException when car ID is not found")
    void testGetById_NotFound() {
        // given
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class,
                () -> carService.getById(1L));

        // then
        assertEquals("Can't find car with current id: 1", exception.getMessage());
        verify(carRepository).findById(1L);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Should retrieve all cars and return a paginated list of DTOs")
    void testGetAll() {
        // given
        Car car1 = CarTestUtil.getCar();
        car1.setBrand("Tesla");

        Car car2 = CarTestUtil.getCar();
        car2.setBrand("Ford");

        Page<Car> carPage = new PageImpl<>(List.of(car1, car2));
        when(carRepository.findAll(any(Pageable.class))).thenReturn(carPage);

        // when
        Page<CarDto> result = carService.getAll(PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo(car1.getBrand());
        assertThat(result.getContent().get(1).getBrand()).isEqualTo(car2.getBrand());
        verify(carRepository).findAll(any(Pageable.class));
        verifyNoMoreInteractions(carRepository);
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
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Should update an existing car and return the updated DTO")
    void testUpdate_Found() {
        // given
        UpdateRequestCarDto updateRequestCarDto = new UpdateRequestCarDto();
        updateRequestCarDto.setInventory(10);
        updateRequestCarDto.setDailyFee(BigDecimal.valueOf(100));

        Car existingCar = CarTestUtil.getCar();
        existingCar.setId(1L);
        existingCar.setInventory(100);
        existingCar.setDailyFee(BigDecimal.valueOf(10));

        Car updatedCar = CarTestUtil.getCar();
        updatedCar.setId(1L);
        updatedCar.setInventory(10);
        updatedCar.setDailyFee(BigDecimal.valueOf(100));

        CarDto expected = CarTestUtil.getCarDto();
        expected.setId(1L);
        expected.setInventory(10);
        expected.setDailyFee(BigDecimal.valueOf(100));

        when(carRepository.findById(1L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);

        // when
        CarDto actual = carService.update(1L, updateRequestCarDto);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(carRepository).findById(1L);
        verify(carRepository).save(existingCar);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Should throw ElementNotFoundException when updating a non-existent car")
    void testUpdate_NotFound() {
        // given
        UpdateRequestCarDto updateRequestCarDto = new UpdateRequestCarDto();
        updateRequestCarDto.setInventory(10);
        updateRequestCarDto.setDailyFee(BigDecimal.valueOf(100));

        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class,
                () -> carService.update(1L, updateRequestCarDto));

        // then
        assertEquals("Can't find car with current id: 1", exception.getMessage());
        verify(carRepository).findById(1L);
        verifyNoMoreInteractions(carRepository);
    }
}
