package org.example.carsharingapi.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequestCarDto {
    @Min(value = 0)
    private int inventory;
    @NotNull
    @Min(value = 0)
    private BigDecimal dailyFee;
}
