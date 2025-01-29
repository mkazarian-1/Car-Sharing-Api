package org.example.carsharingapi.util;

import java.time.LocalDate;
import org.example.carsharingapi.dto.car.CarDto;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.model.Car;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.User;

public class RentalTestUtil {
    public static Rental getRental(Car car, User user) {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now().plusDays(2));
        rental.setReturnDate(LocalDate.now().plusDays(6));

        return rental;
    }

    public static RentalDto getRentalDto(CarDto carDto, Long userId) {
        RentalDto expected = new RentalDto();
        expected.setId(1L);
        expected.setCar(carDto);
        expected.setUserId(userId);
        expected.setRentalDate(LocalDate.now().plusDays(2));
        expected.setReturnDate(LocalDate.now().plusDays(6));
        return expected;
    }
}
