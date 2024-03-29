package searchengine.data.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import searchengine.data.model.Site;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SiteDto {

    private int siteId;
    private String statusName;
    private LocalDateTime statusTime;
    private String lastError;

    public static SiteDto of(Site site) {
        return new SiteDto()
                .setSiteId(site.getId())
                .setStatusName(site.getStatus().name())
                .setStatusTime(site.getStatusTime())
                .setLastError(site.getLastError());
    }
}
