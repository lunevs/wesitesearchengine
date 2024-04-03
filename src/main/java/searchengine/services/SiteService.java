package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.dto.SiteDto;
import searchengine.data.model.Site;
import searchengine.data.model.SiteStatus;
import searchengine.data.repository.JdbcSiteRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final JdbcSiteRepository jdbcSiteRepository;

    public void updateSiteStatus(int siteId, SiteStatus status, String error) {
        SiteDto siteDto = new SiteDto()
                .setSiteId(siteId)
                .setStatusName(status.name())
                .setStatusTime(LocalDateTime.now())
                .setLastError(error);
        jdbcSiteRepository.updateSiteStatus(siteDto);
    }

    public int getSiteByUrlOrCreate(String siteUrl, String siteName) {
        Site savedSite = jdbcSiteRepository.findSiteByUrl(siteUrl);
        if (savedSite == null) {
            return createNewSite(siteUrl, siteName).getId();
        }
        return savedSite.getId();
    }

    public Site createNewSite(String siteUrl, String siteName) {
        Site newSite = new Site()
                .setSiteUrl(siteUrl)
                .setSiteName(siteName);
        int siteId = jdbcSiteRepository.save(siteUrl, siteName);
        return newSite.setId(siteId);
    }
}
