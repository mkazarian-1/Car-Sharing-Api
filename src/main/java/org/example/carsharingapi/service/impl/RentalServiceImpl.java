package org.example.carsharingapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.example.carsharingapi.exeption.ElementNotFoundException;
import org.example.carsharingapi.exeption.IncorrectArgumentException;
import org.example.carsharingapi.mapper.RentalMapper;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.repository.CarRepository;
import org.example.carsharingapi.repository.RentalRepository;
import org.example.carsharingapi.service.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;

    @Override
    public Page<RentalDto> getByUserIdAndActivity(
            Long userId, boolean isActive, Pageable pageable) {
        if (userId == null) {
            return rentalRepository
                    .findRentalsByActivity(isActive, pageable)
                    .map(rentalMapper::toDto);
        }
        return rentalRepository
                .findRentalsByUserIdAndActivity(userId, isActive, pageable)
                .map(rentalMapper::toDto);
    }

    @Override
    public RentalDto getById(Long id) {
        return rentalMapper.toDto(
                rentalRepository.findById(id)
                        .orElseThrow(
                                () -> new ElementNotFoundException(
                                        "Can't find rental with id: " + id)));
    }

    @Override
    public RentalDto getByIdAndUserId(Long id, Long userId) {
        return rentalMapper.toDto(rentalRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(
                        () -> new ElementNotFoundException(
                                "Can't find rental with id: " + id + " and User id " + userId)));
    }

    @Override
    @Transactional
    public RentalDto addRental(CreateRequestRentalDto requestRentalDto, User user) {
        if (!requestRentalDto.getReturnDate().isAfter(requestRentalDto.getRentalDate())) {
            throw new IncorrectArgumentException(
                    "Return date must be after the rental date.");
        }

        Car car = carRepository.findById(requestRentalDto.getCarId())
                .orElseThrow(
                        () -> new ElementNotFoundException(
                                "Can't find car with id: " + requestRentalDto.getCarId()));

        if (car.getInventory() <= 0) {
            throw new IncorrectArgumentException(
                    "Car is not available for rental. Inventory is empty.");
        }

        car.setInventory(car.getInventory() - 1);

        Rental rental = rentalMapper.toEntity(requestRentalDto);
        rental.setCar(car);
        rental.setUser(user);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    @Transactional
    public RentalDto setActualDate(Long id, UpdateRequestRentalDto requestRentalDto, Long userId) {
        Rental rental;
        if (userId == null) {
            rental = rentalRepository.findById(id)
                    .orElseThrow(
                            () -> new ElementNotFoundException("Can't find user with id: " + id));
        } else {
            rental = rentalRepository.findByIdAndUserId(id, userId)
                    .orElseThrow(
                            () -> new ElementNotFoundException(
                                    "Can't find rental with id: " + id + " and User id " + userId));
        }

        if (rental.getActualReturnDate() != null) {
            throw new IncorrectArgumentException(
                    "Rental with id: " + id + " has already been returned");
        }

        if (!requestRentalDto.getActualReturnDate().isAfter(rental.getRentalDate())) {
            throw new IncorrectArgumentException(
                    "Actual return date must be after the rental date.");
        }

        Car car = carRepository.findById(rental.getCar().getId())
                .orElseThrow(() -> new ElementNotFoundException(
                        "Can't find car with id: " + rental.getCar().getId()));
        car.setInventory(car.getInventory() + 1);

        rentalMapper.updateRental(rental, requestRentalDto);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
}
