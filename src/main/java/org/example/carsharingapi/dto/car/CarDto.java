package org.example.carsharingapi.dto.car;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enums.CarType;

@Getter
@Setter
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private CarType type;
    private int inventory;
    private BigDecimal dailyFee;
}
