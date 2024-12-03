package com.example.batch.repository;

import com.example.batch.dto.SettlementOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class SettlementOrderRepositoryTest {

    @Autowired
    private SettlementOrderRepository settlementOrderRepository;

    @Test
    public void testCompareSettlementOrderResults() {
        List<SettlementOrder> orderFromYesterday = settlementOrderRepository.findSettlementOrderForYesterday();
        List<SettlementOrder> order = settlementOrderRepository.findSettlementOrder();

        assertThat(orderFromYesterday).hasSize(order.size());

        for (int i = 0; i < orderFromYesterday.size(); i++) {
            SettlementOrder yesterdayOrder = orderFromYesterday.get(i);
            SettlementOrder settlementOrder = order.get(i);

            assertThat(yesterdayOrder.getOrderId()).isEqualTo(settlementOrder.getOrderId());
            assertThat(yesterdayOrder.getExchangeRate()).isEqualTo(settlementOrder.getExchangeRate());
            assertThat(yesterdayOrder.getTotalPaymentKrw()).isEqualTo(settlementOrder.getTotalPaymentKrw());
        }
    }
}