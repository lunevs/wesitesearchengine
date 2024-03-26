package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repository.JdbcPageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class WebSiteScanHandler {

    private final ForkJoinPool siteScannerPool;
    private final SitesList sitesList;
    private final PagesCashService cashService;
    private final SiteRepository siteRepository;
    private final JdbcPageRepository pageRepository;

    public void startIndexing() {
        if (siteScannerPool != null) {
            Set<WebSiteScanService> services = new HashSet<>();
            sitesList.getSites().forEach(el -> {
                Optional<Site> currentSite = siteRepository.findSiteBySiteUrl(el.getUrl());
                Site savedSite = currentSite.orElseGet(() -> getNewSiteAndSave(el.getName(), el.getUrl()));
                WebSiteScanService service = new WebSiteScanService(savedSite, "/", cashService);
                services.add(service);
                service.fork();
                siteRepository.save(
                        savedSite
                                .setStatus(SiteStatus.INDEXING)
                                .setStatusTime(LocalDateTime.now()));
            });
            siteScannerPool.shutdown();
            services.forEach(service -> {
                Set<String> result = service.join();
                System.out.println("RESULT: " + result.size());
                siteRepository.save(
                        service.getWebSite()
                                .setStatus(SiteStatus.INDEXED)
                                .setStatusTime(LocalDateTime.now()));
                pageRepository.saveAll(cashService.getPagesList());
                cashService.getPagesList().forEach(System.out::println);
            });
        }
    }

    private Site getNewSiteAndSave(String name, String url) {
        Site newSite = new Site()
                .setSiteName(name)
                .setSiteUrl(url)
                .setStatus(SiteStatus.INDEXING)
                .setStatusTime(LocalDateTime.now());
        return siteRepository.save(newSite);
    }

    public void stopIndexing() {
        siteScannerPool.shutdownNow();
    }

}
