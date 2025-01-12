package org.example.carsharingapi.dto.rental;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.dto.car.CarDto;

@Getter
@Setter
public class RentalDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;

    private CarDto car;
    private Long userId;
}
