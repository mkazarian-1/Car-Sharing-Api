package org.example.carsharingapi.dto.rental;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
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
