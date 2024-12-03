package com.example.batch.config;

import com.example.batch.dto.SettlementOrderProduct;
import com.example.batch.repository.SettlementOrderProductRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class Step2Config {

    private final SettlementOrderProductRepository settlementOrderProductRepository;

    public Step2Config(SettlementOrderProductRepository settlementOrderProductRepository) {
        this.settlementOrderProductRepository = settlementOrderProductRepository;
    }

    @Bean
    public ListItemReader<SettlementOrderProduct> readerStep2() {
        List<SettlementOrderProduct> products = settlementOrderProductRepository.findSettlementOrderProductForYesterday();
        return new ListItemReader<>(products);
    }

    @Bean
    public ItemWriter<SettlementOrderProduct> writerStep2() {
        return items -> {
            for (SettlementOrderProduct item : items) {
                settlementOrderProductRepository.saveSettlementOrderProduct(item);
            }
        };
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .<SettlementOrderProduct, SettlementOrderProduct>chunk(1000, transactionManager)
                .reader(readerStep2())
                .writer(writerStep2())
                .build();
    }
}