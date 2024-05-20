package searchengine.services.search;

import lombok.RequiredArgsConstructor;
import searchengine.data.dto.search.LemmaFrequencyDto;
import searchengine.data.repository.JdbcLemmaRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LemmasHolderImpl implements LemmasHolder {

    private final JdbcLemmaRepository lemmaRepository;
    private final SearchQueryHolder searchQueryHolder;

    private static final float EXCLUDE_LIMIT = 0.9F;

    private List<LemmaFrequencyDto> searchLemmasList;

    @Override
    public void load() {
        searchLemmasList = lemmaRepository
                .getLemmasFrequency(searchQueryHolder.getQueryLemmas(), searchQueryHolder.getSearchSiteIds());
        if (searchLemmasList.isEmpty()) {
            throw new IllegalArgumentException("Указанный запрос не найден");
        }
    }

    @Override
    public Set<Integer> filterLemmasIds() {
        return searchLemmasList.stream()
                .filter(el -> el.getLemmaFrequency() < EXCLUDE_LIMIT)
                .sorted(Comparator.comparing(LemmaFrequencyDto::getLemmaFrequency))
                .map(LemmaFrequencyDto::getLemmaId)
                .collect(Collectors.toSet());
    }


}
