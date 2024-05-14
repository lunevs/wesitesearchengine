package searchengine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import searchengine.data.repository.Impl.JdbcLemmaRepositoryImpl;
import searchengine.data.repository.Impl.JdbcPageRepositoryImpl;
import searchengine.data.repository.Impl.JdbcRepositoryImpl;
import searchengine.data.repository.Impl.JdbcSearchIndexRepositoryImpl;
import searchengine.data.repository.Impl.JdbcSearchResultsRepositoryImpl;
import searchengine.data.repository.JdbcLemmaRepository;
import searchengine.data.repository.JdbcPageRepository;
import searchengine.data.repository.JdbcRepository;
import searchengine.data.repository.JdbcSearchIndexRepository;
import searchengine.data.repository.JdbcSearchResultsRepository;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:jdbc-sql.properties")
public class RepositoriesConfiguration {

    @Bean
    public SimpleJdbcInsert pageSimpleJdbcInsert(JdbcTemplate jdbcTemplate) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("page")
                .usingColumns("site_id", "path", "code", "content")
                .usingGeneratedKeyColumns("id");
        insert.compile();
        return insert;
    }

    @Bean
    @ConfigurationProperties("sql.page")
    public JdbcPageRepository jdbcPageRepository(NamedParameterJdbcTemplate jdbcTemplate, SimpleJdbcInsert pageSimpleJdbcInsert) {
        return new JdbcPageRepositoryImpl(jdbcTemplate, pageSimpleJdbcInsert);
    }

    @Bean
    @ConfigurationProperties("sql.index")
    public JdbcSearchIndexRepository jdbcSearchIndexRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcSearchIndexRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @ConfigurationProperties("sql.lemma")
    public JdbcLemmaRepository jdbcLemmaRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcLemmaRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @ConfigurationProperties("sql.results")
    public JdbcSearchResultsRepository jdbcSearchResultsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcSearchResultsRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @ConfigurationProperties("sql.jdbc")
    public JdbcRepository jdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcRepositoryImpl(jdbcTemplate);
    }
}
