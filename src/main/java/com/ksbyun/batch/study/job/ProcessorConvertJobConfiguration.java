package com.ksbyun.batch.study.job;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ksbyun.batch.study.job.model.Teacher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorConvertJobConfiguration {
    public static final String JOB_NAME = "processorConvertBatch";
    public static final String BEAN_PRIFIX = JOB_NAME + "_";
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    
    private static final int CHUNK_SIZE = 1000;
    private static final int PAGE_SIZE = 1000;
    
    @Bean(JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(step())
                .build();
    }
    
    @Bean(BEAN_PRIFIX + "step")
    @JobScope
    public Step step() {
        return stepBuilderFactory.get(BEAN_PRIFIX + "step")
                .<Teacher, String>chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Teacher> reader() {
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PRIFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(PAGE_SIZE)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    @Bean
    public ItemProcessor<Teacher, String> processor() {
        return teacher -> {
            return teacher.getName();
        };
    }

    public ItemWriter<String> writer() {
        return items -> {
            for (String item : items) {
                log.info("Teacher Name = {}", item);
            }
        };
    }
}
