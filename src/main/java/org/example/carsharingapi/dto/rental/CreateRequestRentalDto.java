package org.example.carsharingapi.dto.rental;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestRentalDto {
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Long carId;
}
