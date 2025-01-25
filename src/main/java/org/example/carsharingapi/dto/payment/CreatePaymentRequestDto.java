package org.example.carsharingapi.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enums.PaymentType;

@Getter
@Setter
public class CreatePaymentRequestDto {
    @NotNull
    @Positive
    private Long rentalId;
    @NotNull
    private PaymentType paymentType;
}
