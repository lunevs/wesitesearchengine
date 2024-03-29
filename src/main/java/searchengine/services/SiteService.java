package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.JdbcSiteRepository;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final JdbcSiteRepository jdbcSiteRepository;
    private final ExecutorService fixedExecutorService;

    public void updateSiteStatus(int siteId, SiteStatus status, String error) {
        SiteDto siteDto = new SiteDto()
                .setSiteId(siteId)
                .setStatusName(status.name())
                .setStatusTime(LocalDateTime.now())
                .setLastError(error);
        fixedExecutorService.execute(() -> jdbcSiteRepository.updateSiteStatus(siteDto));
    }
}
