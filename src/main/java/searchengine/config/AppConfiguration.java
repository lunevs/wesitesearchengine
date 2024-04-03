package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfiguration {


//    @Bean(name = "asyncExecutor")
//    public Executor asyncExecutor()  {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(3);
//        executor.setMaxPoolSize(6);
//        executor.setQueueCapacity(300);
//        executor.setThreadNamePrefix("AsynchThread-");
//        executor.initialize();
//        return executor;
//    }

    @Bean("siteScannerExecutorService")
    public ExecutorService siteScannerExecutorService() {
        return Executors.newWorkStealingPool(8);
    }
}
