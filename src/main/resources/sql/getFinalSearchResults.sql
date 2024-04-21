select t.page_id, t.abs_frequency, p.path, p.content, s.url, s.name
from (
         select i.page_id, sum(i.lemma_rank) as abs_frequency
         from search_index i
         where i.lemma_id in (:lemmasIds)
           and i.page_id in (:pagesIds)
         group by i.page_id
     ) t
         join page p on p.id = t.page_id
         join site s on s.id = p.site_id