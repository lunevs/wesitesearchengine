package searchengine.data.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SiteRowMapper implements RowMapper<Site> {

    @Override
    public Site mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Site()
                .setId(rs.getInt("id"))
                .setSiteUrl(rs.getString("url"))
                .setSiteName(rs.getString("name"))
                .setLastError(rs.getString("last_error"));
    }
}
