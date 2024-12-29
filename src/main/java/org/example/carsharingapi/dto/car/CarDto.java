package org.example.carsharingapi.dto.car;

import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enumTypes.CarType;
import java.math.BigDecimal;

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
