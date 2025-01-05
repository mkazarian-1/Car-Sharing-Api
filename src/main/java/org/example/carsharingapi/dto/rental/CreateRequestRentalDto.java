package org.example.carsharingapi.dto.rental;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestRentalDto {
    @NotNull
    @FutureOrPresent
    private LocalDate rentalDate;
    @NotNull
    @Future
    private LocalDate returnDate;
    @NotNull
    @Positive
    private Long carId;
}
