package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.data.dto.DetailedSearchItem;
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.SearchResponse;
import searchengine.services.scanner.PageService;
import searchengine.tools.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final LemmaService lemmaService;
    private final PageService pageService;
    private final SearchQueryHolder searchQueryHolder;
    private final SearchQueryLemmasHolder searchQueryLemmasHolder;
    private final SnippetService snippetService;


    public SearchResponse searchStart(String query, String siteUrl, Integer offset, Integer limit) {
        searchQueryHolder.init(query);
        searchQueryLemmasHolder.init(lemmaService.getLemmasFrequency(searchQueryHolder.getQueryLemmas()));
        if (searchQueryLemmasHolder.getSearchLemmasList().isEmpty()) {
            return SearchResponse.emptyResponse();
        }
        return SearchResponse.of(processSearch(limit));
    }

    private List<DetailedSearchItem> processSearch(Integer limit) {
        Set<Integer> pagesWithAllLemmas = pageService.getPagesWithAllLemmas(searchQueryLemmasHolder.getFilterLemmasIds());
        List<FinalSearchResultDto> searchResults = lemmaService.getFinalSearchResultDto(searchQueryLemmasHolder.getAllLemmasIds(), pagesWithAllLemmas);
        Map<String, Set<Integer>> siteFrequencies = searchQueryLemmasHolder.getResultsFrequencyBySites(searchResults);
        searchResults.forEach(el -> el.setRelFrequency(getRelFrequencyForElement(el, siteFrequencies.get(el.getSiteUrl()))));
        return prepareResults(searchResults, limit);
    }

    private List<DetailedSearchItem> prepareResults(List<FinalSearchResultDto> searchResults, Integer limit) {
        return searchResults.stream()
                .sorted(Comparator.comparingInt(FinalSearchResultDto::getAbsFrequency).reversed())
                .limit(limit)
                .map(this::createDetailedSearchItem)
                .toList();
    }

    private DetailedSearchItem createDetailedSearchItem(FinalSearchResultDto searchResultDto) {
        Document doc = Jsoup.parse(searchResultDto.getPageContent());
        String text = doc.body().text().toLowerCase();
        snippetService.init(text, searchQueryHolder.getQueryLemmasAsList());
        return DetailedSearchItem.of(searchResultDto, doc.title(), snippetService.buildSnippet());
    }

    private double getRelFrequencyForElement(FinalSearchResultDto resultDto, Set<Integer> setAbsFrequencies) {
        return (double) resultDto.getAbsFrequency() / Collections.max(setAbsFrequencies);
    }

}
