package gov.iti.jets.NutriScan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 10 threads always active
        executor.setMaxPoolSize(20); // Max scale of 20 threads
        executor.setQueueCapacity(100); // Max 100 tasks waiting in line
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}