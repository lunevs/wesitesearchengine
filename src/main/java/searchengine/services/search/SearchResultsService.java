package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.data.dto.search.SearchResultsDto;
import searchengine.data.repository.JdbcSearchResultsRepository;
import searchengine.services.scanner.PageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchResultsService {

    private final SearchLemmasHolder searchLemmasHolder;
    private final PageService pageService;
    private final SnippetService snippetService;
    private final JdbcSearchResultsRepository searchResultsRepository;

    private final List<SearchResultsDto> searchResultsList = new ArrayList<>();
    private final Set<Integer> foundPagesIds = new HashSet<>();


    public SearchResultsService findPages() {
        foundPagesIds.clear();
        foundPagesIds.addAll(pageService.getPagesWithAllLemmas(searchLemmasHolder.getFilterLemmasIds()));
        return this;
    }

    public SearchResultsService buildResults() {
        searchResultsList.clear();
        searchResultsList.addAll(searchResultsRepository.findAll(searchLemmasHolder.getFilterLemmasIds(), foundPagesIds));
        return this;
    }

    public SearchResultsService enrichData() {
        Map<String, Set<Integer>> siteFrequencies = searchLemmasHolder.getResultsFrequencyBySites(searchResultsList);
        searchResultsList.forEach(resultsItem -> {
            Document doc = Jsoup.parse(resultsItem.getPageContent());
            String text = doc.body().text().toLowerCase();
            snippetService.init(text);
            resultsItem
                    .setRelFrequency(getRelFrequencyForElement(resultsItem, siteFrequencies.get(resultsItem.getSiteUrl())))
                    .setSnippet(snippetService.buildSnippet())
                    .setPageTitle(doc.title());
        });
        return this;
    }


    public List<SearchResultsDto> getSearchResults() {
        return searchResultsList;
    }

    private double getRelFrequencyForElement(SearchResultsDto resultDto, Set<Integer> setAbsFrequencies) {
        return (double) resultDto.getAbsFrequency() / Collections.max(setAbsFrequencies);
    }

}
