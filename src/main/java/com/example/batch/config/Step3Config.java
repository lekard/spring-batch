package com.example.batch.config;

import com.example.batch.dto.SettlementOrderCSV;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class Step3Config {

    private final JdbcTemplate jdbcTemplate;

    public Step3Config(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Logger logger = LoggerFactory.getLogger(Step3Config.class);

    @Bean
    @JobScope
    public ItemReader<SettlementOrderCSV> readerStep3(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate) {

        logger.info("Job Parameters - Start Date: {}, End Date: {}", startDate, endDate);

        String sql = "SELECT sop.closing_date, sc.settlement_partner_name, sop.artist_id, " +
                "so.order_id, o.currency_code, o.shipping_cost, o.total_payment_amount, " +
                "so.exchange_rate, so.total_payment_krw, op.product_id, " +
                "op.quantity, o.user_country_code, sop.tax_type, op.unit_price, " +
                "sop.total_amount_krw, ROUND(sop.total_amount_krw / sop.supply_rate, 0) AS krw_supply, " +
                "(sop.total_amount_krw - ROUND(sop.total_amount_krw / sop.supply_rate, 0)) AS krw_tax, " +
                "p.settlement_contract_id, sop.consignment_fee, sop.platform_fee " +
                "FROM settlement_order_product sop, order_product op, product p, " +
                "settlement_contract sc, settlement_order so, orders o " +
                "WHERE sop.closing_date BETWEEN ? AND ? " +
                "AND sop.order_product_id = op.order_product_id " +
                "AND op.product_id = p.product_id " +
                "AND p.settlement_contract_id = sc.settlement_contract_id " +
                "AND op.order_id = so.order_id " +
                "AND so.order_id = o.order_id " +
                "ORDER BY sop.closing_date, op.product_id, sc.settlement_partner_name";

        List<SettlementOrderCSV> data = jdbcTemplate.query(sql, ps -> {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
        }, new com.example.batch.mapper.SettlementOrderCSVRowMapper());

        logger.info("Retrieved {} records", data.size());

        return new ListItemReader<>(data);
    }

    @Bean
    public ItemWriter<SettlementOrderCSV> writerStep3() {
        return items -> {
            Map<String, List<SettlementOrderCSV>> groupedData = new HashMap<>();
            for (SettlementOrderCSV item : items) {
                String key = item.getClosingDate() + "_" + item.getProductId() + "_" + item.getSettlementPartnerName();
                groupedData.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }

            for (Map.Entry<String, List<SettlementOrderCSV>> entry : groupedData.entrySet()) {
                logger.info("Group Key: {}, Size: {}", entry.getKey(), entry.getValue().size());

                String groupKey = entry.getKey();
                List<SettlementOrderCSV> groupItems = entry.getValue();
                String filename = "output/" + groupKey + ".csv";

                FlatFileItemWriter<SettlementOrderCSV> csvWriter = new FlatFileItemWriterBuilder<SettlementOrderCSV>()
                        .name("settlementOrderCSVWriter" + groupKey)
                        .resource(new FileSystemResource(filename))
                        .append(false)
                        .lineAggregator(new DelimitedLineAggregator<>() {{
                            setDelimiter(",");
                            setFieldExtractor(item -> new Object[]{
                                    item.getClosingDate(),
                                    item.getSettlementPartnerName(),
                                    item.getArtistId(),
                                    item.getOrderId(),
                                    item.getCurrencyCode(),
                                    item.getShippingCost(),
                                    item.getTotalPaymentAmount(),
                                    item.getExchangeRate(),
                                    item.getTotalPaymentKrw(),
                                    item.getProductId(),
                                    item.getQuantity(),
                                    item.getUserCountryCode(),
                                    item.getTaxType(),
                                    item.getUnitPrice(),
                                    item.getTotalAmountKrw(),
                                    item.getKrwSupply(),
                                    item.getKrwTax(),
                                    item.getSettlementContractId(),
                                    item.getConsignmentFee(),
                                    item.getPlatformFee()
                            });
                        }})
                        .headerCallback(headerWriter -> headerWriter.write("마감일자,정산지급 거래처명,아티스트ID," +
                                "주문ID,통화코드,배송비,총결제금액,환율,총결제금액 원화환산,상품ID,수량,사용자국가코드,세금구분자," +
                                "상품판매 단가금액,원화환산 상품판매가,원화환산 상품판매가 공급가액,원화환산 상품판매가 세금액," +
                                "정산계약ID,CONSIGNMENT 수수료금액,PLATFORM 수수료금액"))
                        .build();

                csvWriter.open(new ExecutionContext());
                try {
                    csvWriter.write(new Chunk<>(groupItems));
                } catch (Exception e) {
                    logger.error("Error writing group: {}", groupKey, e);
                }
                csvWriter.close();
            }
        };
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .<SettlementOrderCSV, SettlementOrderCSV>chunk(100000, transactionManager)
                .reader(readerStep3(null, null))
                .writer(writerStep3())
                .build();
    }
}