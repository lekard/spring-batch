package com.example.batch.config;

import com.example.batch.dto.SettlementOrder;
import com.example.batch.repository.SettlementOrderRepository;
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
public class Step1Config {

    private final SettlementOrderRepository settlementOrderRepository;

    public Step1Config(SettlementOrderRepository settlementOrderRepository) {
        this.settlementOrderRepository = settlementOrderRepository;
    }

    @Bean
    public ListItemReader<SettlementOrder> readerStep1() {
        List<SettlementOrder> orders = settlementOrderRepository.findSettlementOrderForYesterday();
        return new ListItemReader<>(orders);
    }

    @Bean
    public ItemWriter<SettlementOrder> writerStep1() {
        return items -> {
            for (SettlementOrder item : items) {
                settlementOrderRepository.saveSettlementOrder(item);
            }
        };
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<SettlementOrder, SettlementOrder>chunk(1000, transactionManager)
                .reader(readerStep1())
                .writer(writerStep1())
                .build();
    }
}