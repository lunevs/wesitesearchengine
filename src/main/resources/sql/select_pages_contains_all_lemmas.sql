select p.id, p.site_id, p.path as page_path, p.content as page_content
from (
    select page_id, count(*) as cnt
    from search_index i
    where lemma_id in (:lemmas)
    group by page_id
    having count(*) = :lemmasCount
) t
join page p on t.page_id = p.id;
