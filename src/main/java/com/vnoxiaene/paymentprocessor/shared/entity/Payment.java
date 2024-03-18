package com.vnoxiaene.paymentprocessor.shared.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String billingCode;

    private BigDecimal amount;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING; // Status inicial padrão

    @ManyToOne
    private Seller seller;
}
