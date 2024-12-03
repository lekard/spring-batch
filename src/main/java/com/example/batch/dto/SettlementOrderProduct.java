package com.example.batch.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "settlement_order_product")
public class SettlementOrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    @Column(name = "order_product_id", nullable = false)
    private Integer orderProductId;

    @Column(name = "artist_id", nullable = false)
    private Integer artistId;

    @Column(name = "total_amount_krw", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmountKrw;

    @Column(name = "tax_type", nullable = false, length = 50)
    private String taxType;

    @Column(name = "supply_rate", nullable = false, precision = 3, scale = 2)
    private BigDecimal supplyRate;

    @Column(name = "consignment_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal consignmentFee;

    @Column(name = "platform_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal platformFee;

}