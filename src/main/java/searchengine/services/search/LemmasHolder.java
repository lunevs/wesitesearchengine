package searchengine.services.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.data.dto.search.LemmaFrequencyDto;
import searchengine.data.repository.JdbcLemmaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
@Slf4j
@RequiredArgsConstructor
public class LemmasHolder {

    private final JdbcLemmaRepository lemmaRepository;
    private final SearchQueryHolder searchQueryHolder;

    private static final float EXCLUDE_LIMIT = 0.9F;

    private List<LemmaFrequencyDto> searchLemmasList;

    public void load() {
        searchLemmasList = lemmaRepository.getLemmasFrequency(searchQueryHolder.getQueryLemmas(), searchQueryHolder.getSearchSiteIds());
        if (searchLemmasList.isEmpty()) {
            throw new IllegalArgumentException("Указанный запрос не найден");
        }
    }

    public Set<Integer> filterLemmasIds() {
        return searchLemmasList.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }


}
