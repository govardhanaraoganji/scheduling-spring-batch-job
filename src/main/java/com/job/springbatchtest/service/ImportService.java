package com.job.springbatchtest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ImportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

    private final JobLauncher jobLauncher;
    private final Job importJob;

    public ImportService(final JobLauncher jobLauncher, final Job importJob) {
        this.jobLauncher = jobLauncher;
        this.importJob = importJob;
    }

    public void startJob() {
        try {
            jobLauncher.run(importJob, new JobParametersBuilder().addString("uuid", UUID.randomUUID().toString()).toJobParameters());
        } catch (JobExecutionException e) {
            LOGGER.error("Execution failed", e);
        }
    }
}
