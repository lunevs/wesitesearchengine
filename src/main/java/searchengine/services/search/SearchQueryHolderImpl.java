package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import searchengine.data.model.Site;
import searchengine.services.common.LemmaParser;
import searchengine.services.scanner.SiteService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class SearchQueryHolderImpl implements SearchQueryHolder {

    private final LemmaParser lemmaParser;
    private final SiteService siteService;

    private Set<String> queryLemmas;
    private List<Integer> searchSiteIds;

    @Override
    public void init(String query, String siteUrl) {
        queryLemmas = lemmaParser.collectLemmas(query).keySet();
        searchSiteIds = (siteUrl != null && !siteUrl.isBlank()) ?
            List.of(siteService
                    .findSiteByUrl(siteUrl)
                    .orElseThrow(() -> new IllegalArgumentException("Указанный сайт не проиндексирован"))
                    .getId())
            : siteService.findAll().stream().map(Site::getId).toList();
    }

    @Override
    public List<String> getQueryAsLemmaList() {
        return queryLemmas.stream().toList();
    }

    @Override
    public Set<String> getQueryLemmas() {
        return queryLemmas;
    }

    @Override
    public List<Integer> getSearchSiteIds() {
        return searchSiteIds;
    }


}
