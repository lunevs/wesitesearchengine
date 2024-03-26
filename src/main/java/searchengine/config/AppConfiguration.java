package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class AppConfiguration {

    @Bean
    public ForkJoinPool siteScannerPool() {
        return new ForkJoinPool();
    }

}
