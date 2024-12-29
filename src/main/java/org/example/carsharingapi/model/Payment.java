package org.example.carsharingapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enumTypes.PaymentStatus;
import org.example.carsharingapi.model.enumTypes.PaymentType;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @Column(nullable = false)
    private BigDecimal amountToPay;

    //TODO -- payment session credential
}

