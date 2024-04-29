select
    t.page_id
     , t.abs_frequency
     , p.path as page_path
     , p.content as page_content
     , s.url as site_url
     , s.name as site_name
from (
         select i.page_id, sum(i.lemma_rank) as abs_frequency
         from search_index i
         where i.lemma_id in (:lemmasIds)
           and i.page_id in (:pagesIds)
         group by i.page_id
     ) t
         join page p on p.id = t.page_id
         join site s on s.id = p.site_id