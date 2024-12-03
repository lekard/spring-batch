package com.example.batch.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SettlementOrderCSV {
    private LocalDate closingDate;
    private String settlementPartnerName;
    private Integer artistId;
    private Integer orderId;
    private String currencyCode;
    private BigDecimal shippingCost;
    private BigDecimal totalPaymentAmount;
    private BigDecimal exchangeRate;
    private BigDecimal totalPaymentKrw;
    private Integer productId;
    private Integer quantity;
    private String userCountryCode;
    private String taxType;
    private BigDecimal unitPrice;
    private BigDecimal totalAmountKrw;
    private BigDecimal krwSupply;
    private BigDecimal krwTax;
    private Integer settlementContractId;
    private BigDecimal consignmentFee;
    private BigDecimal platformFee;
}