package searchengine.services.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.data.model.Site;
import searchengine.services.common.LemmaParser;
import searchengine.services.scanner.SiteService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Getter
public class SearchQueryHolder {

    private final LemmaParser lemmaParser;
    private final SiteService siteService;

    private String initialQuery;
    private Set<String> queryLemmas;
    private List<Integer> searchSiteIds;

    public void init(String query, String siteUrl) {
        initialQuery = query;
        queryLemmas = lemmaParser.collectLemmas(query).keySet();
        searchSiteIds = (siteUrl != null && !siteUrl.isBlank()) ?
            List.of(siteService
                    .findSiteByUrl(siteUrl)
                    .orElseThrow(() -> new IllegalArgumentException("Указанный сайт не проиндексирован"))
                    .getId())
            : siteService.findAll().stream().map(Site::getId).toList();
    }

    public List<String> getQueryAsLemmaList() {
        return queryLemmas.stream().toList();
    }



}
