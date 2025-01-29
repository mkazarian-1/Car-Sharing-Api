package org.example.carsharingapi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.example.carsharingapi.exeption.ElementNotFoundException;
import org.example.carsharingapi.mapper.RentalMapper;
import org.example.carsharingapi.mapper.impl.CarMapperImpl;
import org.example.carsharingapi.mapper.impl.RentalMapperImpl;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.CarRepository;
import org.example.carsharingapi.repository.RentalRepository;
import org.example.carsharingapi.util.CarTestUtil;
import org.example.carsharingapi.util.RentalTestUtil;
import org.example.carsharingapi.util.UserTestUtil;
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
class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Spy
    private final RentalMapper rentalMapper = new RentalMapperImpl(new CarMapperImpl());

    @Test
    @DisplayName("Should retrieve rentals by user ID and activity")
    void testGetByUserIdAndActivity() {
        // given
        Car car = CarTestUtil.getCar();
        User user = UserTestUtil.getUser();
        Rental rental = RentalTestUtil.getRental(car, user);

        CarDto carDto = CarTestUtil.getCarDto();
        RentalDto expected = RentalTestUtil.getRentalDto(carDto, user.getId());

        boolean isActive = true;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));
        when(rentalRepository
                .findRentalsByUserIdAndActivity(user.getId(), isActive, pageable))
                .thenReturn(rentalPage);

        // when
        Page<RentalDto> actual = rentalService
                .getByUserIdAndActivity(user.getId(), isActive, pageable);

        // then
        assertEquals(1, actual.getTotalElements());
        assertTrue(EqualsBuilder
                .reflectionEquals(expected, actual.getContent().get(0), List.of("car", "userId")));
        verify(rentalRepository).findRentalsByUserIdAndActivity(user.getId(), isActive, pageable);
    }

    @Test
    @DisplayName("Should throw ElementNotFoundException when rental is not found by ID")
    void testGetById_NotFound() {
        // given
        Long rentalId = 1L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        // when
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class,
                () -> rentalService.getById(rentalId));

        // then
        assertEquals("Can't find rental with id: " + rentalId, exception.getMessage());
        verify(rentalRepository).findById(rentalId);
    }

    @Test
    @DisplayName("Should add a rental successfully")
    void testAddRental() {
        // given
        CreateRequestRentalDto requestRentalDto = new CreateRequestRentalDto();
        requestRentalDto.setCarId(1L);
        requestRentalDto.setRentalDate(LocalDate.now().plusDays(2));
        requestRentalDto.setReturnDate(LocalDate.now().plusDays(6));

        Car car = CarTestUtil.getCar();
        car.setInventory(1);

        User user = UserTestUtil.getUser();

        Rental rental = RentalTestUtil.getRental(car, user);

        CarDto carDto = CarTestUtil.getCarDto();
        carDto.setInventory(0);

        RentalDto expected = RentalTestUtil.getRentalDto(carDto, user.getId());

        when(carRepository.findById(requestRentalDto.getCarId())).thenReturn(Optional.of(car));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // when
        RentalDto actual = rentalService.addRental(requestRentalDto, user);

        // then
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, List.of("car", "userId")));
        assertEquals(expected.getCar().getInventory(), actual.getCar().getInventory());
        verify(carRepository).findById(requestRentalDto.getCarId());
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    @DisplayName("Should set actual return date and update inventory")
    void testSetActualDate() {
        // given
        UpdateRequestRentalDto requestRentalDto = new UpdateRequestRentalDto();
        requestRentalDto.setActualReturnDate(LocalDate.now().plusDays(7));

        Car car = CarTestUtil.getCar();
        car.setInventory(0);

        Rental rental = RentalTestUtil.getRental(car, null);

        CarDto carDto = CarTestUtil.getCarDto();
        carDto.setInventory(1);

        RentalDto expected = RentalTestUtil.getRentalDto(carDto, null);
        expected.setActualReturnDate(requestRentalDto.getActualReturnDate());

        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // when
        RentalDto actual = rentalService.setActualDate(rental.getId(), requestRentalDto, null);

        // then
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, List.of("car", "userId")));
        assertEquals(expected.getCar().getInventory(), actual.getCar().getInventory());
        verify(rentalRepository).findById(rental.getId());
        verify(carRepository).findById(car.getId());
        verify(rentalRepository).save(rental);
    }
}
