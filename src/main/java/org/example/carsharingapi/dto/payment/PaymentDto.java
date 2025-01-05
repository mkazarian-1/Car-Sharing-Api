package org.example.carsharingapi.dto.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.dto.rental.RentalDto;
import org.example.carsharingapi.model.Rental;
import org.example.carsharingapi.model.enums.PaymentStatus;
import org.example.carsharingapi.model.enums.PaymentType;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentDto {
    private Long id;
    private PaymentStatus status;
    private PaymentType type;

    private RentalDto rental;
    private BigDecimal amountToPay;

    private String sessionUrl;
    private String sessionId;
}
