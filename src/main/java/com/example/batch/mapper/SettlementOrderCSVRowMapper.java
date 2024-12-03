package com.example.batch.mapper;

import com.example.batch.dto.SettlementOrderCSV;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettlementOrderCSVRowMapper implements RowMapper<SettlementOrderCSV> {

    @Override
    public SettlementOrderCSV mapRow(ResultSet rs, int rowNum) throws SQLException {
        SettlementOrderCSV settlementOrderCSV = new SettlementOrderCSV();
        settlementOrderCSV.setClosingDate(rs.getDate("closing_date").toLocalDate());
        settlementOrderCSV.setSettlementPartnerName(rs.getString("settlement_partner_name"));
        settlementOrderCSV.setArtistId(rs.getInt("artist_id"));
        settlementOrderCSV.setOrderId(rs.getInt("order_id"));
        settlementOrderCSV.setCurrencyCode(rs.getString("currency_code"));
        settlementOrderCSV.setShippingCost(rs.getBigDecimal("shipping_cost"));
        settlementOrderCSV.setTotalPaymentAmount(rs.getBigDecimal("total_payment_amount"));
        settlementOrderCSV.setExchangeRate(rs.getBigDecimal("exchange_rate"));
        settlementOrderCSV.setTotalPaymentKrw(rs.getBigDecimal("total_payment_krw"));
        settlementOrderCSV.setProductId(rs.getInt("product_id"));
        settlementOrderCSV.setQuantity(rs.getInt("quantity"));
        settlementOrderCSV.setUserCountryCode(rs.getString("user_country_code"));
        settlementOrderCSV.setTaxType(rs.getString("tax_type"));
        settlementOrderCSV.setUnitPrice(rs.getBigDecimal("unit_price"));
        settlementOrderCSV.setTotalAmountKrw(rs.getBigDecimal("total_amount_krw"));
        settlementOrderCSV.setKrwSupply(rs.getBigDecimal("krw_supply"));
        settlementOrderCSV.setKrwTax(rs.getBigDecimal("krw_tax"));
        settlementOrderCSV.setSettlementContractId(rs.getInt("settlement_contract_id"));
        settlementOrderCSV.setConsignmentFee(rs.getBigDecimal("consignment_fee"));
        settlementOrderCSV.setPlatformFee(rs.getBigDecimal("platform_fee"));
        return settlementOrderCSV;
    }
}