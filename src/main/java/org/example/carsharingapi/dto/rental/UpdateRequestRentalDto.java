package org.example.carsharingapi.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequestRentalDto {
    @NotNull
    private LocalDate actualReturnDate;
}
