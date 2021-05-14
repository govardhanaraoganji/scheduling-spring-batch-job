package com.job.springbatchtest.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class JobImportConfiguration {
    private static Logger LOGGER = LoggerFactory.getLogger(JobImportConfiguration.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JobImportConfiguration(
            JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job importJob(final Step taskletStep) {
        return jobBuilderFactory
                .get("importJob")
                .incrementer(new RunIdIncrementer())
                .start(taskletStep)
                .build();
    }

    @Bean
    public Step taskletStep(final Tasklet tasklet) {
        return stepBuilderFactory
                .get("taskletStep")
                .tasklet(tasklet)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    @StepScope
    @Value("#{jobParameters['uuid']}")
    public Tasklet tasklet(final String uniqueId) {
        return (stepContribution, chunkContext) -> {
            LOGGER.info("tasklet executed " + uniqueId);
            return RepeatStatus.FINISHED;
        };
    }
}
