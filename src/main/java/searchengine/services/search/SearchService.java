package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.data.dto.DetailedSearchItem;
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.LemmaFrequencyDto;
import searchengine.data.dto.PageDto;
import searchengine.data.dto.SearchResponse;
import searchengine.services.scanner.PageService;
import searchengine.tools.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private static final float EXCLUDE_LIMIT = 0.9F;

    private final LemmaParserService lemmaParserService;
    private final LemmaService lemmaService;
    private final PageService pageService;

    public SearchResponse searchStart(String query, String siteUrl, Integer offset, Integer limit) {
        Set<String> queryWords = lemmaParserService.collectLemmas(query).keySet();
        List<LemmaFrequencyDto> searchLemmas = lemmaService.getLemmasFrequency(queryWords);
        List<LemmaFrequencyDto> filteredLemmas = searchLemmas.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .toList();
        Set<Integer> filteredLemmaIds = filteredLemmas.stream().map(LemmaFrequencyDto::getLemmaId).collect(Collectors.toSet());

        if (filteredLemmaIds.isEmpty()) {
            return new SearchResponse(false, 0, null);
        }
        List<FinalSearchResultDto> finalSearchResults = getFinalSearchResultDto(filteredLemmaIds, searchLemmas);
        List<DetailedSearchItem> detailedSearchItemList = finalSearchResults.stream()
                .sorted(Comparator.comparingInt(FinalSearchResultDto::getAbsFrequency).reversed())
                .limit(20)
                .map(el -> formatDetailedSearchItem(el,queryWords))
                .toList();
        return new SearchResponse(
                !detailedSearchItemList.isEmpty(),
                detailedSearchItemList.size(),
                detailedSearchItemList);

//                    .forEach(el -> {
//                        Document doc = Jsoup.parse(el.getPageContent());
//                        String text = doc.body().text().toLowerCase();
//                        String snippet = getSnippet(text, queryWords.stream().toList());
//                        DetailedSearchItem item = new DetailedSearchItem(el.getSiteUrl(), el.getSiteName(), el.getPagePath(), doc.title(), snippet, el.getRelFrequency());
//                        detailedSearchItemList.add(item);
//                    });

    }

    private DetailedSearchItem formatDetailedSearchItem(FinalSearchResultDto searchResultDto, Set<String> queryWords) {
        Document doc = Jsoup.parse(searchResultDto.getPageContent());
        String text = doc.body().text().toLowerCase();
        String snippet = getSnippet(text, queryWords.stream().toList());
        return new DetailedSearchItem(searchResultDto.getSiteUrl(), searchResultDto.getSiteName(), searchResultDto.getPagePath(), doc.title(), snippet, searchResultDto.getRelFrequency());
    }

    private List<FinalSearchResultDto> getFinalSearchResultDto(Set<Integer> filteredLemmaIds, List<LemmaFrequencyDto> searchLemmas) {
        List<PageDto> resultPages = pageService.getPagesWithAllLemmas(filteredLemmaIds);
        Set<Integer> allLemmasIds = searchLemmas.stream().map(LemmaFrequencyDto::getLemmaId).collect(Collectors.toSet());
        List<FinalSearchResultDto> result = lemmaService.getFinalSearchResultDto(
                allLemmasIds,
                resultPages.stream().map(PageDto::getId).collect(Collectors.toSet()));

        Map<String, Set<Integer>> siteFrequencies = result.stream()
                .collect(Collectors.groupingBy(
                        FinalSearchResultDto::getSiteUrl,
                        Collectors.mapping(FinalSearchResultDto::getAbsFrequency, Collectors.toSet())));
        for (Map.Entry<String, Set<Integer>> entry : siteFrequencies.entrySet()) {
            Optional<Integer> max = siteFrequencies.get(entry.getKey()).stream().max(Float::compare);
            max.ifPresent(value -> result.forEach(el -> el.setRelFrequency((double) el.getAbsFrequency() / value)));
        }
        return result;
    }

    public String getSnippet(String text, List<String> words) {
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
