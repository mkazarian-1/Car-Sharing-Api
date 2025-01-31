package org.example.carsharingapi.repository;

import java.time.LocalDate;
import java.util.List;
import org.example.carsharingapi.model.Rental;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("""
            Find rentals with Pageable from empty DB
            """)
    void findRentalsByUserIdAndActivity_EmptyDb_Successful() {
        Page<Rental> actual = rentalRepository
                .findRentalsByUserIdAndActivity(
                        1L, true, PageRequest.of(0, 10));
        Assertions.assertEquals(0, actual.getTotalElements());
    }

    @Test
    @DisplayName("""
            Find all rentals with Pageable, userId and activity
            """)
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findRentalsByUserIdAndActivity_Successful() {
        Page<Rental> actual1 = rentalRepository
                .findRentalsByUserIdAndActivity(
                        3L, true, PageRequest.of(0, 10));
        Assertions.assertEquals(2, actual1.getTotalElements());

        Page<Rental> actual2 = rentalRepository
                .findRentalsByUserIdAndActivity(
                        2L, true, PageRequest.of(0, 10));
        Assertions.assertEquals(1, actual2.getTotalElements());

        Page<Rental> actual3 = rentalRepository
                .findRentalsByUserIdAndActivity(
                        3L, false, PageRequest.of(0, 10));
        Assertions.assertEquals(1, actual3.getTotalElements());

        Page<Rental> actual4 = rentalRepository
                .findRentalsByUserIdAndActivity(
                        2L, false, PageRequest.of(0, 10));
        Assertions.assertEquals(2, actual4.getTotalElements());
    }

    @Test
    @DisplayName("""
            Find all rentals with Pageable, userId and activity
            """)
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findRentalsByActivity() {
        Page<Rental> actual1 = rentalRepository
                .findRentalsByActivity(true, PageRequest.of(0, 10));
        Assertions.assertEquals(3, actual1.getTotalElements());

        Page<Rental> actual2 = rentalRepository
                .findRentalsByActivity(false, PageRequest.of(0, 10));
        Assertions.assertEquals(3, actual2.getTotalElements());
    }

    @Test
    @DisplayName("""
            Find all rentals with Pageable, userId and activity
            """)
    @Sql(scripts = "classpath:rentals/add-rentals-with-user-car.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:rentals/delete-rentals-users-cars.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findOverdueRentals() {
        List<Rental> actual = rentalRepository
                .findOverdueRentals(LocalDate.parse("2025-10-01"));
        Assertions.assertEquals(2, actual.size());

        List<Rental> actual1 = rentalRepository
                .findOverdueRentals(LocalDate.parse("2027-10-01"));
        Assertions.assertEquals(3, actual1.size());
    }
}
