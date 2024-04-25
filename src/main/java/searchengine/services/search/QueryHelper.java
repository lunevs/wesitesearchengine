package searchengine.services.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class QueryHelper {

    private final LemmaParserService lemmaParserService;

    private String initialQuery;
    private Set<String> queryLemmas;

    public void init(String query) {
        initialQuery = query;
        queryLemmas = lemmaParserService.collectLemmas(query).keySet();
    }

    public List<String> getQueryLemmasAsList() {
        return queryLemmas.stream().toList();
    }



}
