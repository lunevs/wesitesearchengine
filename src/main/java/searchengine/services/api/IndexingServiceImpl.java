package searchengine.services.api;

import lombok.RequiredArgsConstructor;
import searchengine.config.SitesList;
import searchengine.data.dto.api.DefaultResponse;
import searchengine.data.dto.scanner.ScanTaskDto;
import searchengine.data.model.SiteParameters;
import searchengine.services.common.ExecutorServiceHandler;
import searchengine.services.scanner.SiteScannerService;
import searchengine.services.scanner.SiteService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private final ExecutorServiceHandler executorServiceHandler;
    private final SiteScannerService scannerService;
    private final SiteService siteService;

    private final SitesList sites;

    private ScanTaskDto taskForScan;

    @Override
    public DefaultResponse startIndexing() {
        if (executorServiceHandler.isActive()) {
            return new DefaultResponse(false, "Индексация уже запущена");
        }

        executorServiceHandler.reload();
        if (taskForScan == null) {
            scannerService.startAllSitesScan(sites);
        } else {
            scannerService.startOnePageScan(taskForScan);
        }
        return new DefaultResponse(true, null);
    }

    @Override
    public DefaultResponse stopIndexing() {
        if (executorServiceHandler.isActive()) {
            scannerService.unexpectedStop();
            return new DefaultResponse(true, null);
        } else {
            return new DefaultResponse(false, "Индексация не запущена");
        }
    }

    @Override
    public DefaultResponse indexPage(String url) {
        if (executorServiceHandler.isActive()) {
            return new DefaultResponse(false, "Индексация уже запущена");
        }

        String addUrl = URLDecoder.decode(url, StandardCharsets.UTF_8).trim();
        Optional<SiteParameters> parameters = sites.getSites().stream().filter(s -> addUrl.startsWith(s.getUrl())).findFirst();
        if (parameters.isPresent()) {
            siteService
                    .findSiteByUrl(parameters.get().getUrl())
                    .ifPresent(site -> taskForScan = new ScanTaskDto(site.getSiteUrl(), addUrl.substring(site.getSiteUrl().length()), site.getId()));
            return new DefaultResponse(true, null);
        } else {
            return new DefaultResponse(false, """
                    Данная страница находится за пределами сайтов,\s
                    указанных в конфигурационном файле
                    """);
        }
    }

}
