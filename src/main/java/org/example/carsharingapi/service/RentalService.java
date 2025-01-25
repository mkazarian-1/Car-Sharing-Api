package org.example.carsharingapi.service;

import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.example.carsharingapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    Page<RentalDto> getByUserIdAndActivity(Long userId, boolean active, Pageable pageable);

    RentalDto getById(Long id);

    RentalDto getByIdAndUserId(Long id, Long userId);

    RentalDto addRental(CreateRequestRentalDto requestRentalDto, User user);

    RentalDto setActualDate(Long id, UpdateRequestRentalDto requestRentalDto, Long userId);
}
