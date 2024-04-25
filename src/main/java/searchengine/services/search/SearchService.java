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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {


    private final LemmaParserService lemmaParserService;
    private final LemmaService lemmaService;
    private final PageService pageService;
    private final QueryHelper queryHelper;
    private final SearchResultHelper searchResultHelper;


    public SearchResponse searchStart(String query, String siteUrl, Integer offset, Integer limit) {
        queryHelper.init(query);
        searchResultHelper.init(lemmaService.getLemmasFrequency(queryHelper.getQueryLemmas()));
        if (searchResultHelper.getSearchLemmasFrequency().isEmpty()) {
            return SearchResponse.emptyResponse();
        }
        return SearchResponse.of(processSearch(limit));
    }

    private List<DetailedSearchItem> processSearch(Integer limit) {
        Set<Integer> pagesWithAllLemmas = pageService.getPagesWithAllLemmas(searchResultHelper.getFilterLemmasIds());
        List<FinalSearchResultDto> searchResults = lemmaService.getFinalSearchResultDto(searchResultHelper.getAllLemmasIds(), pagesWithAllLemmas);
        Map<String, Set<Integer>> siteFrequencies = searchResultHelper.getResultsFrequencyBySites(searchResults);
        searchResults.forEach(el -> el.setRelFrequency(getRelFrequencyForElement(el, siteFrequencies.get(el.getSiteUrl()))));
//        for (Map.Entry<String, Set<Integer>> entry : siteFrequencies.entrySet()) {
//            Optional<Integer> max = siteFrequencies.get(entry.getKey()).stream().max(Float::compare);
//            max.ifPresent(value -> searchResults.forEach(el -> el.setRelFrequency((double) el.getAbsFrequency() / value)));
//        }
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
        String snippet = createSnippet(text, queryHelper.getQueryLemmasAsList());
        return DetailedSearchItem.of(searchResultDto, doc.title(), snippet);
    }

    private double getRelFrequencyForElement(FinalSearchResultDto resultDto, Set<Integer> setAbsFrequencies) {
        return (double) resultDto.getAbsFrequency() / Collections.max(setAbsFrequencies);
    }

    public String createSnippet(String text, List<String> words) {
        Map<String, Set<String>> searchFormWords = getNormalFormsMap(text, words);
        List<NavigableSet<Integer>> indexes = findWordsIndexes(text, searchFormWords);
        List<Integer> indexCombination = findClosestWordCombination(indexes);
        String snippet = StringUtils.createSnippet(text, indexCombination);
        return makeSearchWordsBold(indexCombination, text, snippet);
    }


    private Integer getClosestElement(Integer baseEl, Integer el1, Integer el2) {
        if (el1 == null && el2 == null) {
            return null;
        } else if (el1 == null || el2 == null) {
            return el1 == null ? el2 : el1;
        } else {
            return Math.abs(el1 - baseEl) < Math.abs(el2 - baseEl) ? el1 : el2;
        }
    }

    private Map<String, Set<String>> getNormalFormsMap(String text, List<String> words) {
        Map<String, Set<String>> searchFormWords = new HashMap<>();
        words.forEach(w -> searchFormWords.put(w, new HashSet<>()));

        for (String curWord : text.split("[\\s+\\-]")) {
            String normalForm = lemmaParserService.getNormalWordForm(curWord);
            int wordIndex;
            if (!normalForm.isEmpty() && (wordIndex = words.indexOf(normalForm)) != -1) {
                searchFormWords.get(words.get(wordIndex)).add(curWord);
            }
        }
        return searchFormWords;
    }

    private List<Integer> findClosestWordCombination(List<NavigableSet<Integer>> indexes)  {
        List<Integer> indexCombination = new ArrayList<>();
        NavigableSet<Integer> baseSet = indexes.remove(0);
        if (baseSet == null || baseSet.isEmpty()) {
            return new ArrayList<>();
        }
        for (Integer curIndex : baseSet) {
            List<Integer> curCombination = new ArrayList<>(List.of(curIndex));
            for (NavigableSet<Integer> curSet : indexes) {
                Integer closest = getClosestElement(curIndex, curSet.ceiling(curIndex), curSet.floor(curIndex));
                if (closest != null) {
                    curCombination.add(closest);
                }
            }
            if (indexCombination.isEmpty()) {
                indexCombination.addAll(curCombination);
            } else {
                if (getCombinationLength(curCombination) < getCombinationLength(indexCombination)) {
                    indexCombination = new ArrayList<>(curCombination);
                }
            }
        }
        return indexCombination.stream().sorted().toList();
    }

    private int getCombinationLength(List<Integer> combination) {
        return combination.stream().max(Integer::compareTo).orElse(Integer.MAX_VALUE) - combination.stream().min(Integer::compareTo).orElse(0);
    }

    private List<NavigableSet<Integer>> findWordsIndexes(String text, Map<String, Set<String>> searchFormWords) {
        List<NavigableSet<Integer>> indexes = new ArrayList<>(searchFormWords.size());
        for (Map.Entry<String, Set<String>> searchElement : searchFormWords.entrySet()) {
            NavigableSet<Integer> curIndexes = new TreeSet<>();
            for (String s : searchElement.getValue()) {
                s = s.toLowerCase();
                int k = -1;
                while((k = text.indexOf(s, k+1)) != -1) {
                    curIndexes.add(k);
                }
            }
            indexes.add(curIndexes);
        }
        return indexes;
    }

    private String makeSearchWordsBold(List<Integer> indexCombination, String text, String snippet) {
        for (int wordStartIndex : indexCombination) {
            int wordEndIndex = text.indexOf(" ", wordStartIndex+1);
            String curWordToBold = text.substring(wordStartIndex, wordEndIndex);
            snippet = snippet.replace(curWordToBold, MessageFormat.format("<b>{0}</b>", curWordToBold));
        }
        return snippet;
    }


}
