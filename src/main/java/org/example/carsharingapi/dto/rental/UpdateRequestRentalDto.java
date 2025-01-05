package org.example.carsharingapi.dto.rental;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequestRentalDto {
    @NotNull
    private LocalDate actualReturnDate;
}
