package com.job.springbatchtest.config;

import com.job.springbatchtest.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Configuration
@EnableScheduling
public class ImportScheduleConfiguration implements SchedulingConfigurer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ImportScheduleConfiguration.class);

    private final ImportService importService;

    public ImportScheduleConfiguration(final ImportService importService) {
        this.importService = importService;
    }

    @Bean(destroyMethod = "shutdown")
    public Executor stockImportTaskExecutor() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("import-async");
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(stockImportTaskExecutor());
        List<Integer> list = new ArrayList<>();
        for(int index=0; index < 100; index++){
            list.add(index+1);
        }
        list.stream().forEach( index -> scheduledTaskRegistrar.addCronTask(
                () -> {
                    LOGGER.info("Starting import process for index {}", index);
                    importService.startJob();
                    LOGGER.info("End of import process for index {}", index);
                },
                "0 */2 * * * ?")
        );
    }
}
