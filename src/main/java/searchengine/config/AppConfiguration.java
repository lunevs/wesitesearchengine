package searchengine.config;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AppConfiguration {


    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutor()  {
        return Executors.newFixedThreadPool(4);
    }

    @Bean("siteScannerExecutorService")
    public ExecutorService siteScannerExecutorService() {
        return Executors.newWorkStealingPool(8);
    }

    @Bean
    public LuceneMorphology luceneMorphology() throws IOException {
        return new RussianLuceneMorphology();
    }

}
