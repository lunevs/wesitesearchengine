package searchengine.data.repository;

import java.util.ArrayList;
import java.util.List;

public interface AbstractBatchRepository<T> {

//    private final SimpleJdbcInsert simpleJdbcInsert;
//    NamedParameterJdbcTemplate jdbcTemplate;

//    public T save(T dto) {
//        SqlParameterSource params = new BeanPropertySqlParameterSource(dto);
//        Number id = simpleJdbcInsert.executeAndReturnKey(params);
//        dto.setId(id);
//        return dto;
//    }

    default void saveAll(List<T> pages, String sql) {
        splitByBatches(pages, 100, sql);
    }

    void batchUpdate(List<T> pages, String sql);

    private void splitByBatches(List<T> items, int batchSize, String sql) {
        int cnt = 0;
        List<T> batch = new ArrayList<>(batchSize);
        for (T item : items) {
            if (++cnt % batchSize == 0) {
                batchUpdate(batch, sql);
                batch = new ArrayList<>(batchSize);
            }
            batch.add(item);
        }
        batchUpdate(batch, sql);
    }
}
