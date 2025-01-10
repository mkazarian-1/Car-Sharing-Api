package org.example.carsharingapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.carsharingapi.dto.rental.CreateRequestRentalDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.dto.rental.UpdateRequestRentalDto;
import org.example.carsharingapi.model.User;
import org.example.carsharingapi.model.enums.UserRole;
import org.example.carsharingapi.security.util.UserUtil;
import org.example.carsharingapi.service.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @GetMapping()
    public Page<RentalDto> getByUserIdAndActivity(
            @RequestParam(name = "user_id", required = false,
                    defaultValue = "#{null}") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true")
            boolean isActive,
            Pageable pageable
    ) {
        User user = UserUtil.getAuthenticatedUser();

        if (user.getRoles().contains(UserRole.MANAGER)) {
            return rentalService.getByUserIdAndActivity(
                    userId, isActive, pageable);
        }
        return rentalService.getByUserIdAndActivity(
                user.getId(), isActive, pageable);
    }

    @GetMapping("/{id}")
    public RentalDto getById(@PathVariable Long id) {
        User user = UserUtil.getAuthenticatedUser();
        if (user.getRoles().contains(UserRole.MANAGER)) {
            return rentalService.getById(id);
        }
        return rentalService.getByIdAndUserId(id, user.getId());
    }

    @PostMapping()
    public RentalDto addRental(@RequestBody @Valid CreateRequestRentalDto requestRentalDto) {
        User user = UserUtil.getAuthenticatedUser();
        return rentalService.addRental(requestRentalDto, user);
    }

    @PostMapping("/return/{id}")
    public RentalDto setActualDate(@PathVariable Long id,
                            @RequestBody @Valid UpdateRequestRentalDto requestRentalDto) {
        User user = UserUtil.getAuthenticatedUser();
        if (user.getRoles().contains(UserRole.MANAGER)) {
            return rentalService.setActualDate(id, requestRentalDto, null);
        }
        return rentalService.setActualDate(id, requestRentalDto, user.getId());
    }
}
