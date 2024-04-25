package searchengine.services.search;

import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.data.dto.DetailedSearchItem;
import searchengine.data.dto.FinalSearchResultDto;
import searchengine.data.dto.LemmaFrequencyDto;
import searchengine.data.dto.SearchResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
public class SearchResultHelper {

    private static final float EXCLUDE_LIMIT = 0.9F;

    private List<LemmaFrequencyDto> searchLemmasFrequency;

    public void init(List<LemmaFrequencyDto> searchLemmasFrequency) {
        this.searchLemmasFrequency = searchLemmasFrequency;
    }

    public Set<Integer> getFilterLemmasIds() {
        return searchLemmasFrequency.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }

    public Set<Integer> getAllLemmasIds() {
        return searchLemmasFrequency.stream()
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }

    public Map<String, Set<Integer>> getResultsFrequencyBySites(List<FinalSearchResultDto> searchResults) {
        return searchResults.stream()
                .collect(Collectors.groupingBy(
                        FinalSearchResultDto::getSiteUrl,
                        Collectors.mapping(FinalSearchResultDto::getAbsFrequency, Collectors.toSet())));
    }

}
