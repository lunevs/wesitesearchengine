package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.data.dto.search.SearchResultsDto;
import searchengine.data.repository.JdbcSearchResultsRepository;
import searchengine.services.scanner.PageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SearchResultsServiceImpl implements SearchResultsService {

    private final LemmasHolder lemmasHolder;
    private final PageService pageService;
    private final SnippetService snippetService;
    private final JdbcSearchResultsRepository searchResultsRepository;

    private List<SearchResultsDto> searchResultsList = new ArrayList<>();

    @Override
    public void findPages() {
        Set<Integer> foundPagesIds = pageService.findPagesWithAllLemmas(lemmasHolder.filterLemmasIds());
        if (!foundPagesIds.isEmpty()) {
            searchResultsList = searchResultsRepository.findAll(lemmasHolder.filterLemmasIds(), foundPagesIds);
        }
    }

    @Override
    public void enrichData() {
        Map<String, Set<Integer>> siteFrequencies = getResultsFrequencyBySites(searchResultsList);
        searchResultsList.forEach(resultsItem -> {
            Document doc = Jsoup.parse(resultsItem.getPageContent());
            String text = doc.body().text().toLowerCase();
            snippetService.init(text);
            resultsItem
                    .setRelFrequency(getRelFrequencyForElement(resultsItem, siteFrequencies.get(resultsItem.getSiteUrl())))
                    .setSnippet(snippetService.buildSnippet())
                    .setPageTitle(doc.title());
        });
    }

    @Override
    public List<SearchResultsDto> getSearchResults() {
        return searchResultsList;
    }

    private double getRelFrequencyForElement(SearchResultsDto resultDto, Set<Integer> setAbsFrequencies) {
        return (double) resultDto.getAbsFrequency() / Collections.max(setAbsFrequencies);
    }

    private Map<String, Set<Integer>> getResultsFrequencyBySites(List<SearchResultsDto> searchResults) {
        return searchResults.stream()
                .collect(Collectors.groupingBy(
                        SearchResultsDto::getSiteUrl,
                        Collectors.mapping(SearchResultsDto::getAbsFrequency, Collectors.toSet())));
    }

}
