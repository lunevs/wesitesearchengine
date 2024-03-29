package searchengine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import searchengine.data.Impl.JdbcPageRepositoryImpl;
import searchengine.data.Impl.JdbcSiteRepositoryImpl;
import searchengine.data.repository.JdbcPageRepository;
import searchengine.data.repository.JdbcSiteRepository;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:jdbc-sql.properties")
public class RepositoriesConfiguration {

    @Bean
    @ConfigurationProperties("sql.site")
    public JdbcSiteRepository jdbcSiteRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcSiteRepositoryImpl(jdbcTemplate);
    }

    @Bean
    @ConfigurationProperties("sql.page")
    public JdbcPageRepository jdbcPageRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        return new JdbcPageRepositoryImpl(jdbcTemplate);
    }
}
