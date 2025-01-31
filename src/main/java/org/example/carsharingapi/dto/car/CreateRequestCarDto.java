package org.example.carsharingapi.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enums.CarType;

@Getter
@Setter
public class CreateRequestCarDto {
    @NotBlank
    @Size(max = 255)
    private String model;
    @NotBlank
    @Size(max = 255)
    private String brand;
    @NotNull
    private CarType type;
    @Min(value = 0)
    private int inventory;
    @NotNull
    @Min(value = 0)
    private BigDecimal dailyFee;
}
