package com.example.batch.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "settlement_order")
public class SettlementOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "exchange_rate", nullable = false, precision = 10, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "total_payment_krw", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPaymentKrw;

}