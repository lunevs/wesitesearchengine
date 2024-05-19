package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import searchengine.services.api.IndexingService;
import searchengine.services.api.IndexingServiceImpl;
import searchengine.services.api.SearchStarterService;
import searchengine.services.api.SearchStarterServiceImpl;
import searchengine.services.common.ExecutorServiceHandler;
import searchengine.services.scanner.SiteScannerService;
import searchengine.services.scanner.SiteService;
import searchengine.services.search.SearchService;

@Configuration
public class ServicesBeansConfiguration {

    @Bean
    public SearchStarterService searchStarterService(SearchService searchService) {
        return new SearchStarterServiceImpl(searchService);
    }

    @Bean
    public IndexingService indexingService(ExecutorServiceHandler executorServiceHandler, SiteScannerService scannerService, SiteService siteService, SitesList sites) {
        return new IndexingServiceImpl(executorServiceHandler, scannerService, siteService, sites);
    }
}
