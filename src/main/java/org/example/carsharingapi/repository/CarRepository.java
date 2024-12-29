package org.example.carsharingapi.repository;

import org.example.carsharingapi.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {
}
