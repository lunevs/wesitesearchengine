package searchengine.config;

import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import searchengine.data.repository.JdbcLemmaRepository;
import searchengine.data.repository.JdbcRepository;
import searchengine.data.repository.JdbcSearchIndexRepository;
import searchengine.data.repository.PageRepository;
import searchengine.services.api.IndexingService;
import searchengine.services.api.IndexingServiceImpl;
import searchengine.services.api.SearchStarterService;
import searchengine.services.api.SearchStarterServiceImpl;
import searchengine.services.api.StatisticsService;
import searchengine.services.api.StatisticsServiceImpl;
import searchengine.services.common.ExecutorServiceHandler;
import searchengine.services.common.LemmaParser;
import searchengine.services.common.LemmaParserImpl;
import searchengine.services.common.LemmaService;
import searchengine.services.common.LemmaServiceImpl;
import searchengine.services.scanner.PageParser;
import searchengine.services.scanner.PageParserImpl;
import searchengine.services.scanner.PageService;
import searchengine.services.scanner.PageServiceImpl;
import searchengine.services.scanner.SearchIndexService;
import searchengine.services.scanner.SearchIndexServiceImpl;
import searchengine.services.scanner.SiteScannerService;
import searchengine.services.scanner.SiteScannerServiceImpl;
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

    @Bean
    public LemmaService lemmaService(JdbcLemmaRepository lemmaRepository) {
        return new LemmaServiceImpl(lemmaRepository);
    }

    @Bean
    public LemmaParser lemmaParser(LuceneMorphology luceneMorphology, SearchIndexService searchIndexService, LemmaService lemmaService) {
        return new LemmaParserImpl(luceneMorphology, searchIndexService, lemmaService);
    }

    @Bean
    public PageParser pageParser(ExecutorServiceHandler executorServiceHandler, PageService pageService, LemmaParser lemmaParser) {
        return new PageParserImpl(executorServiceHandler, pageService, lemmaParser);
    }

    @Bean
    public PageService pageService(PageRepository pageRepository, JdbcRepository jdbcRepository, SimpleJdbcInsert pageSimpleJdbcInsert) {
        return new PageServiceImpl(pageRepository, jdbcRepository, pageSimpleJdbcInsert);
    }

    @Bean
    public SearchIndexService searchIndexService(JdbcSearchIndexRepository searchIndexRepository) {
        return new SearchIndexServiceImpl(searchIndexRepository);
    }

    @Bean
    public SiteScannerService siteScannerService(SiteService siteService, ExecutorServiceHandler executorServiceHandler, PageParser pageParser) {
        return new SiteScannerServiceImpl(siteService, executorServiceHandler, pageParser);
    }

    @Bean
    public StatisticsService statisticsService(SiteService siteService, JdbcRepository jdbcRepository) {
        return new StatisticsServiceImpl(siteService, jdbcRepository);
    }
}
