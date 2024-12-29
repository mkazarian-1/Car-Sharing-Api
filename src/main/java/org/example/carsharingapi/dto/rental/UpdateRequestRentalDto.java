package org.example.carsharingapi.dto.rental;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateRequestRentalDto {
    private LocalDate actualReturnDate;
}
