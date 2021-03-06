package com.ksbyun.batch.study.job;

import java.util.Random;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeciderJobConfiguration {
    private final static String EVEN = "EVEN";
    private final static String ODD = "ODD";
    
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public Job deciderJob() {
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider())                // 홀수 | 짝수 구분
                .from(decider())                // decider의 상태가
                    .on(DeciderJobConfiguration.ODD)    // ODD면
                    .to(oddStep())                      // oddStep()으로
                .from(decider())                // decider의 상태가
                    .on(DeciderJobConfiguration.EVEN)   // EVEN이면
                    .to(evenStep())                     // evenStep()으로
                .end()
                .build();
    }

    @Bean
    public Step startStep() {
        return stepBuilderFactory.get("startStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> Start!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> 홀수입니다.");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    public Step evenStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> 짝수입니다.");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }
    
    public static class OddDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random rand = new Random();
            
            int randomNumber = rand.nextInt(99) + 1;    // 1~100
            log.info("Random Number : {}", randomNumber);
            
            if (randomNumber % 2 == 0) {
                return new FlowExecutionStatus(DeciderJobConfiguration.EVEN);
            } else {
                return new FlowExecutionStatus(DeciderJobConfiguration.ODD);
            }
        }
    }
}
