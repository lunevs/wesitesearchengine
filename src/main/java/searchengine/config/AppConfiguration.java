package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import searchengine.data.dto.PageDto;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class AppConfiguration {

    @Bean
    public ForkJoinPool siteScannerPool() {
        return new ForkJoinPool();
    }

    @Bean
    public ExecutorService siteScannerExecutorService() {
        return Executors.newWorkStealingPool();
    }

    @Bean
    public ExecutorService fixedExecutorService() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean
    public Set<PageDto> batchSetOfPages() {
        return ConcurrentHashMap.newKeySet();
    }
}
