package com.example.batch.repository;

import com.example.batch.dto.SettlementOrderProduct;
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
public class SettlementOrderProductRepositoryTest {

    @Autowired
    private SettlementOrderProductRepository settlementOrderProductRepository;

    @Test
    public void testCompareSettlementOrderProductResults() {
        List<SettlementOrderProduct> productFromYesterday = settlementOrderProductRepository.findSettlementOrderProductForYesterday();
        List<SettlementOrderProduct> product = settlementOrderProductRepository.findSettlementOrderProduct();

        assertThat(productFromYesterday).hasSize(product.size());

        for (int i = 0; i < productFromYesterday.size(); i++) {
            SettlementOrderProduct yesterdayProduct = productFromYesterday.get(i);
            SettlementOrderProduct savedProduct = product.get(i);

            assertThat(yesterdayProduct.getOrderProductId()).isEqualTo(savedProduct.getOrderProductId());
            assertThat(yesterdayProduct.getArtistId()).isEqualTo(savedProduct.getArtistId());
            assertThat(yesterdayProduct.getTotalAmountKrw()).isEqualTo(savedProduct.getTotalAmountKrw());
            assertThat(yesterdayProduct.getTaxType()).isEqualTo(savedProduct.getTaxType());
            assertThat(yesterdayProduct.getSupplyRate()).isEqualTo(savedProduct.getSupplyRate());
            assertThat(yesterdayProduct.getConsignmentFee()).isEqualTo(savedProduct.getConsignmentFee());
            assertThat(yesterdayProduct.getPlatformFee()).isEqualTo(savedProduct.getPlatformFee());
        }
    }
}
