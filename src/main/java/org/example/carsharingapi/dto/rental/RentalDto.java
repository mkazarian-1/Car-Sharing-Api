package org.example.carsharingapi.dto.rental;

import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.enumTypes.CarType;

import java.math.BigDecimal;
import java.time.LocalDate;

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
