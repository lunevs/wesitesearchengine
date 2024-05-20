package searchengine.services.search;

import java.util.List;
import java.util.Set;

public interface SearchQueryHolder {

    void init(String query, String siteUrl);
    List<String> getQueryAsLemmaList();
    Set<String> getQueryLemmas();
    List<Integer> getSearchSiteIds();
}
