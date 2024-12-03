package com.example.batch.repository;

import com.example.batch.dto.SettlementOrderProduct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SettlementOrderProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public SettlementOrderProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SettlementOrderProduct> findSettlementOrderProductForYesterday() {
        String sql = "SELECT CURDATE() - INTERVAL 1 DAY AS closing_date, op.order_product_id, p.artist_id, " +
                "ROUND(op.quantity * op.unit_price * krw_exchange_rate, 0) AS total_amount_krw, " +
                "IFNULL(tr.tax_type, 'X') AS tax_type, IFNULL(tr.supply_rate, 1) AS supply_rate, " +
                "IFNULL(ROUND(scd1.exchanged_price * scd1.fee_rate, 0), 0) AS consignment_fee, " +
                "IFNULL(ROUND((scd1.exchanged_price - scd1.exchanged_price * scd1.fee_rate) * scd2.fee_rate, 0), 0) AS platform_fee " +
                "FROM order_product op, exchange_rate er, product p " +
                "LEFT JOIN settlement_contract_detail scd1 ON p.settlement_contract_id = scd1.settlement_contract_id " +
                "AND scd1.fee_item = 'CONSIGNMENT' " +
                "LEFT JOIN settlement_contract_detail scd2 ON p.settlement_contract_id = scd2.settlement_contract_id " +
                "AND scd2.fee_item = 'PLATFORM', " +
                "orders o LEFT JOIN tax_rate tr ON o.user_country_code = tr.country_code " +
                "AND DATE_FORMAT(o.payment_datetime, '%Y-%m-%d') BETWEEN tr.start_date AND tr.end_date " +
                "WHERE op.order_id = o.order_id " +
                "AND DATE_FORMAT(o.payment_datetime, '%Y-%m-%d') = CURDATE() - INTERVAL 1 DAY " +
                "AND op.product_id = p.product_id " +
                "AND o.currency_code = er.currency_code " +
                "AND DATE_FORMAT(o.payment_datetime, '%Y-%m-%d') = er.transaction_date " +
                "ORDER BY order_product_id";

        return jdbcTemplate.query(sql, new SettlementOrderProductRowMapper());
    }

    public void saveSettlementOrderProduct(SettlementOrderProduct product) {
        String sql = "INSERT INTO settlement_order_product (closing_date, order_product_id, artist_id, " +
                "total_amount_krw, tax_type, supply_rate, " +
                "consignment_fee, platform_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.getClosingDate(), product.getOrderProductId(), product.getArtistId(),
                product.getTotalAmountKrw(), product.getTaxType(), product.getSupplyRate(),
                product.getConsignmentFee(), product.getPlatformFee());
    }

    public List<SettlementOrderProduct> findSettlementOrderProduct() {
        String sql = "SELECT closing_date, order_product_id, artist_id, " +
                "ROUND(total_amount_krw,0) AS total_amount_krw, " +
                "tax_type, supply_rate, " +
                "ROUND(consignment_fee,0) AS consignment_fee, " +
                "ROUND(platform_fee,0) AS platform_fee " +
                "FROM settlement_order_product " +
                "WHERE closing_date = CURDATE() - INTERVAL 1 DAY " +
                "ORDER BY order_product_id";

        return jdbcTemplate.query(sql, new SettlementOrderProductRowMapper());
    }

    private static class SettlementOrderProductRowMapper implements RowMapper<SettlementOrderProduct> {
        @Override
        public SettlementOrderProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
            SettlementOrderProduct product = new SettlementOrderProduct();
            product.setClosingDate(rs.getDate("closing_date").toLocalDate());
            product.setOrderProductId(rs.getInt("order_product_id"));
            product.setArtistId(rs.getInt("artist_id"));
            product.setTotalAmountKrw(rs.getBigDecimal("total_amount_krw"));
            product.setTaxType(rs.getString("tax_type"));
            product.setSupplyRate(rs.getBigDecimal("supply_rate"));
            product.setConsignmentFee(rs.getBigDecimal("consignment_fee"));
            product.setPlatformFee(rs.getBigDecimal("platform_fee"));
            return product;
        }
    }
}