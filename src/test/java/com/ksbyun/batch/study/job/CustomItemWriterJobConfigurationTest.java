package com.ksbyun.batch.study.job;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@EnableBatchProcessing
@SpringBatchTest
@SpringBootTest(classes={CustomItemWriterJobConfiguration.class})
public class CustomItemWriterJobConfigurationTest {
    
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void test() throws Exception {
        // given
        JobParameters jobParameter = new JobParametersBuilder()
                .addString("--job.name", "customItemWriterJob")
                .addString("version", "9")
                .toJobParameters();
        
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameter);
        
        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
