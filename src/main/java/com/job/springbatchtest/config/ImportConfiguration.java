package com.job.springbatchtest.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ImportConfiguration {

    @Bean
    @BatchDataSource
    public DataSource hsqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:testdb;sql.enforce_strict_size=true;hsqldb.tx=mvcc");
        dataSource.setPassword("");
        dataSource.setUsername("sa");
        return dataSource;
    }

    @Bean
    @Primary
    public JobRepository hsqlJobRepository(DataSource hsqlDataSource) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDatabaseType(EmbeddedDatabaseType.HSQL.name());
        factory.setDataSource(hsqlDataSource);
        factory.setTransactionManager(getTransactionManager());
        factory.setValidateTransactionState(true);
        factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }

    @Bean
    @Primary
    public JobLauncher hsqlJobLauncher(final JobRepository hsqlJobRepository) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(hsqlJobRepository);
//        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    private PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }
}
