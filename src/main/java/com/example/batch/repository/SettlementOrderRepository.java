package com.example.batch.repository;

import com.example.batch.dto.SettlementOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SettlementOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public SettlementOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SettlementOrder> findSettlementOrderForYesterday() {
        String sql = "SELECT CURDATE() - INTERVAL 1 DAY AS closing_date, order_id, krw_exchange_rate AS exchange_rate, " +
                "ROUND(o.total_payment_amount * krw_exchange_rate, 0) AS total_payment_krw " +
                "FROM orders o, exchange_rate er " +
                "WHERE DATE_FORMAT(payment_datetime, '%Y-%m-%d') = CURDATE() - INTERVAL 1 DAY " +
                "AND o.currency_code = er.currency_code " +
                "AND DATE_FORMAT(o.payment_datetime, '%Y-%m-%d') = er.transaction_date " +
                "ORDER BY order_id";

        return jdbcTemplate.query(sql, new SettlementOrderRowMapper());
    }

    public void saveSettlementOrder(SettlementOrder order) {
        String sql = "INSERT INTO settlement_order (closing_date, order_id, exchange_rate, total_payment_krw) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, order.getClosingDate(), order.getOrderId(), order.getExchangeRate(), order.getTotalPaymentKrw());
    }

    public List<SettlementOrder> findSettlementOrder() {
        String sql = "SELECT closing_date, order_id, exchange_rate, ROUND(total_payment_krw, 0) AS total_payment_krw " +
                "FROM settlement_order " +
                "WHERE closing_date = CURDATE() - INTERVAL 1 DAY " +
                "ORDER BY order_id";

        return jdbcTemplate.query(sql, new SettlementOrderRowMapper());
    }

    private static class SettlementOrderRowMapper implements RowMapper<SettlementOrder> {
        @Override
        public SettlementOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            SettlementOrder order = new SettlementOrder();
            order.setClosingDate(rs.getDate("closing_date").toLocalDate());
            order.setOrderId(rs.getInt("order_id"));
            order.setExchangeRate(rs.getBigDecimal("exchange_rate"));
            order.setTotalPaymentKrw(rs.getBigDecimal("total_payment_krw"));
            return order;
        }
    }
}