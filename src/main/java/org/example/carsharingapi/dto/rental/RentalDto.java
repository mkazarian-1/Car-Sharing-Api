package org.example.carsharingapi.dto.rental;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.Car;

@Getter
@Setter
public class RentalDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;

    private Car car;
    private Long userId;
}
