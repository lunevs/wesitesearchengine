package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.data.dto.DetailedSearchItem;
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.LemmaFrequencyDto;
import searchengine.data.dto.PageDto;
import searchengine.data.dto.SearchResponse;
import searchengine.services.scanner.PageService;

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
        List<DetailedSearchItem> detailedSearchItemList = new ArrayList<>();
        Set<String> queryWords = lemmaParserService.collectLemmas(query).keySet();
        log.info("initial search string {}", query);
        log.info("search lemmas: {}", queryWords);

        List<LemmaFrequencyDto> searchLemmas = lemmaService.getLemmasFrequency(queryWords);
        List<LemmaFrequencyDto> filteredLemmas = searchLemmas.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .toList();
        Set<Integer> filteredLemmaIds = filteredLemmas.stream().map(LemmaFrequencyDto::getLemmaId).collect(Collectors.toSet());

        if (!filteredLemmaIds.isEmpty()) {
            List<PageDto> resultPages = pageService.getPagesWithAllLemmas(filteredLemmaIds);
            resultPages.forEach(page -> log.info(page.getPagePath()));
            log.info("search pages ids: {}", resultPages.stream().map(PageDto::getId).collect(Collectors.toSet()));
            Set<Integer> allLemmasIds = searchLemmas.stream().map(LemmaFrequencyDto::getLemmaId).collect(Collectors.toSet());
            log.info("search lemmas ids: {}", allLemmasIds);
            List<FinalSearchResultDto> result = lemmaService.getFinalSearchResultDto(
                    allLemmasIds,
                    resultPages.stream().map(PageDto::getId).collect(Collectors.toSet()));
            log.info("result pages: {}", result.size());

            Map<String, Set<Integer>> siteFrequencies = result.stream()
                    .collect(Collectors.groupingBy(
                            FinalSearchResultDto::getSiteUrl,
                            Collectors.mapping(FinalSearchResultDto::getAbsFrequency, Collectors.toSet())));
            for (Map.Entry<String, Set<Integer>> entry : siteFrequencies.entrySet()) {
                Optional<Integer> max = siteFrequencies.get(entry.getKey()).stream().max(Float::compare);
                max.ifPresent(value -> result.forEach(el -> el.setRelFrequency((double) el.getAbsFrequency() / value)));
            }
            result.stream()
                    .sorted(Comparator.comparingInt(FinalSearchResultDto::getAbsFrequency).reversed())
                    .limit(20)
                    .forEach(el -> {
                        Document doc = Jsoup.parse(el.getPageContent());
                        String text = doc.body().text().toLowerCase();
                        log.info("{}\t{}\t{}\t{}\t{}", el.getSiteUrl(), el.getPagePath(), el.getAbsFrequency(), el.getRelFrequency(), doc.title());
                        String snippet = getIndexes(text, queryWords.stream().toList());
                        log.info("snippet: {}", snippet);
                        DetailedSearchItem item = new DetailedSearchItem(el.getSiteUrl(), el.getSiteName(), el.getPagePath(), doc.title(), snippet, el.getRelFrequency());
                        detailedSearchItemList.add(item);
                    });
        }

        return new SearchResponse(
                !detailedSearchItemList.isEmpty(),
                detailedSearchItemList.size(),
                detailedSearchItemList
        );
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

    public String getIndexes(String text, List<String> words) {
        String snippet = "";
        Map<String, Set<String>> searchFormWords = new HashMap<>();
        words.forEach(w -> searchFormWords.put(w, new HashSet<>()));

        for (String curWord : text.split("[\\s+\\-]")) {
            String normalForm = lemmaParserService.getNormalWordForm(curWord);
            int wordIndex;
            if (!normalForm.isEmpty() && (wordIndex = words.indexOf(normalForm)) != -1) {
                searchFormWords.get(words.get(wordIndex)).add(curWord);
            }
        }

        List<NavigableSet<Integer>> indexes = new ArrayList<>(words.size());
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
            log.info("{} indexes = {}", searchElement.getKey(), curIndexes);
        }
        List<List<Integer>> indexCombinations = new ArrayList<>();
        NavigableSet<Integer> baseSet = indexes.remove(0);
        if (baseSet != null) {
            for (Integer curIndex : baseSet) {
                List<Integer> curCombination = new ArrayList<>();
                curCombination.add(curIndex);
                for (NavigableSet<Integer> curSet : indexes) {
                    Integer closest = getClosestElement(curIndex, curSet.ceiling(curIndex), curSet.floor(curIndex));
                    if (closest == null) {
                        continue;
                    }
                    curCombination.add(closest);
                }
                if (indexCombinations.isEmpty()) {
                    indexCombinations.add(curCombination);
                } else {
                    List<Integer> oldCombination = indexCombinations.get(0);
                    int oldCombinationLen = oldCombination.stream().max(Integer::compareTo).orElse(Integer.MAX_VALUE) - oldCombination.stream().min(Integer::compareTo).orElse(0);
                    int newCombinationLen = curCombination.stream().max(Integer::compareTo).orElse(Integer.MAX_VALUE) - curCombination.stream().min(Integer::compareTo).orElse(0);
                    if (newCombinationLen < oldCombinationLen) {
                        indexCombinations.remove(0);
                        indexCombinations.add(curCombination);
                    }
                }
            }
            List<Integer> resultCombination = indexCombinations.get(0);
            int minIndex = resultCombination.stream().min(Integer::compareTo).orElse(0);
            int maxIndex = resultCombination.stream().max(Integer::compareTo).orElse(text.length());
            int fromIndex = minIndex < 70 ? 0 : minIndex - 70;
            int toIndex = maxIndex > text.length()-70 ? text.length() : maxIndex + 70;
            String tmpSnippet = text.substring(fromIndex, toIndex);
            snippet = tmpSnippet.substring(tmpSnippet.indexOf(" "), tmpSnippet.lastIndexOf(" "));
        }
        return snippet;
    }
}
