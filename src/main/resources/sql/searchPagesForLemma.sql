select i.lemma_id, l.lemma, l.site_id, s.url, i.page_id, p.path, lemma_rank, l.frequency
from search_index i
         join lemma l on i.lemma_id = l.id
         join search_engine.site s on s.id = l.site_id
         join search_engine.page p on l.site_id = p.site_id and p.id = i.page_id
where l.lemma = :lemmaName