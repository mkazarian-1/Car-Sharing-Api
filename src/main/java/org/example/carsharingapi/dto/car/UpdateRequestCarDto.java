package org.example.carsharingapi.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enumTypes.CarType;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateRequestCarDto {
    @Min(value = 0)
    private int inventory;
    @NotNull
    @Min(value = 0)
    private BigDecimal dailyFee;
}
