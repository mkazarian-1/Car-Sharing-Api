package org.example.carsharingapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.example.carsharingapi.mapper.RentalMapper;
import org.example.carsharingapi.mapper.impl.CarMapperImpl;
import org.example.carsharingapi.mapper.impl.RentalMapperImpl;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.CarRepository;
import org.example.carsharingapi.repository.RentalRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        Long userId = 1L;
        boolean isActive = true;
        Pageable pageable = PageRequest.of(0, 10);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now().plusDays(1));
        rental.setReturnDate(LocalDate.now().plusDays(5));

        RentalDto expected = new RentalDto();
        expected.setId(1L);
        expected.setRentalDate(LocalDate.now().plusDays(1));
        expected.setReturnDate(LocalDate.now().plusDays(5));

        Page<Rental> rentalPage = new PageImpl<>(List.of(rental));
        when(rentalRepository.findRentalsByUserIdAndActivity(userId, isActive, pageable))
                .thenReturn(rentalPage);

        // when
        Page<RentalDto> actual = rentalService.getByUserIdAndActivity(userId, isActive, pageable);

        // then
        assertEquals(1, actual.getTotalElements());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual.getContent().get(0)));
        verify(rentalRepository).findRentalsByUserIdAndActivity(userId, isActive, pageable);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when rental is not found by ID")
    void testGetById_NotFound() {
        // given
        Long rentalId = 1L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
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
        requestRentalDto.setRentalDate(LocalDate.now().plusDays(1));
        requestRentalDto.setReturnDate(LocalDate.now().plusDays(5));

        Car car = new Car();
        car.setId(1L);
        car.setInventory(1);

        User user = new User();
        user.setId(1L);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);
        rental.setUser(user);
        rental.setRentalDate(requestRentalDto.getRentalDate());
        rental.setReturnDate(requestRentalDto.getReturnDate());

        CarDto carDto = new CarDto();
        carDto.setId(1L);
        carDto.setInventory(0);

        RentalDto expected = new RentalDto();
        expected.setId(1L);
        expected.setCar(carDto);
        expected.setUserId(1L);
        expected.setRentalDate(requestRentalDto.getRentalDate());
        expected.setReturnDate(requestRentalDto.getReturnDate());

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
    @DisplayName("Should throw IllegalArgumentException when adding a rental with a past rental date")
    void testAddRental_PastRentalDate() {
        // given
        CreateRequestRentalDto requestRentalDto = new CreateRequestRentalDto();
        requestRentalDto.setCarId(1L);
        requestRentalDto.setRentalDate(LocalDate.now().minusDays(1));
        requestRentalDto.setReturnDate(LocalDate.now().plusDays(5));

        User user = new User();
        user.setId(1L);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rentalService.addRental(requestRentalDto, user));

        // then
        assertEquals("Rental date can't be in the past.", exception.getMessage());
        verifyNoInteractions(carRepository, rentalRepository);
    }

    @Test
    @DisplayName("Should set actual return date and update inventory")
    void testSetActualDate() {
        // given
        Long rentalId = 1L;
        UpdateRequestRentalDto requestRentalDto = new UpdateRequestRentalDto();
        requestRentalDto.setActualReturnDate(LocalDate.now().plusDays(6));

        Car car = new Car();
        car.setId(1L);
        car.setInventory(0);

        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setCar(car);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(1));
        rental.setActualReturnDate(null);

        CarDto carDto = new CarDto();
        carDto.setId(1L);
        carDto.setInventory(1);

        RentalDto expected = new RentalDto();
        expected.setId(rentalId);
        expected.setCar(carDto);
        expected.setUserId(1L);
        expected.setRentalDate(rental.getRentalDate());
        expected.setReturnDate(rental.getReturnDate());
        expected.setActualReturnDate(requestRentalDto.getActualReturnDate());

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // when
        RentalDto actual = rentalService.setActualDate(rentalId, requestRentalDto, null);

        // then
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, List.of("car", "userId")));
        assertEquals(expected.getCar().getInventory(), actual.getCar().getInventory());
        verify(rentalRepository).findById(rentalId);
        verify(carRepository).findById(car.getId());
        verify(rentalRepository).save(rental);
    }
}
