select l.site_id, l.id, l.lemma, t1.total_pages, t2.total_pages_with_lemma
from lemma l
         join (
    select p.site_id, count(p.id) as total_pages
    from page p
    group by p.site_id
) t1 on l.site_id = t1.site_id
         join (
    select i.lemma_id, count(i.page_id) as total_pages_with_lemma
    from search_index i
    group by i.lemma_id
) t2 on t2.lemma_id = l.id
         join (
    select l.site_id, count(l.id) as lemma_num
    from lemma l
    where l.lemma in (:lemmas)
    group by l.site_id
) t3 on l.site_id = t3.site_id and t3.lemma_num = :lemmasCount
where l.lemma in (:lemmas)
