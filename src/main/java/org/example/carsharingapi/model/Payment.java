package org.example.carsharingapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.example.carsharingapi.model.enums.PaymentStatus;
import org.example.carsharingapi.model.enums.PaymentType;

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

    @Column(nullable = false)
    private String sessionUrl;
    @Column(nullable = false)
    private String sessionId;
}
