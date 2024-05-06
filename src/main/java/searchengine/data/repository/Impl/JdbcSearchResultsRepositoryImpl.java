package searchengine.data.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import searchengine.data.dto.SearchResultsDto;
import searchengine.data.repository.JdbcSearchResultsRepository;
import searchengine.tools.ResourceUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Setter
public class JdbcSearchResultsRepositoryImpl implements JdbcSearchResultsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private Resource getFinalSearchResults;


    @Override
    public List<SearchResultsDto> findAll(Set<Integer> lemmasIds, Set<Integer> pagesIds) {
        return jdbcTemplate.query(
                ResourceUtils.getString(getFinalSearchResults),
                Map.of("lemmasIds", lemmasIds, "pagesIds", pagesIds),
                new BeanPropertyRowMapper<>(SearchResultsDto.class));

    }
}
