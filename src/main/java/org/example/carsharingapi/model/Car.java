package org.example.carsharingapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enumTypes.CarType;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CarType type;

    private int inventory;

    @Column(nullable = false)
    private BigDecimal dailyFee;
}
