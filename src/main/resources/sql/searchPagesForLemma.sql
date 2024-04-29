select
    i.lemma_id
     , l.lemma as lemma_name
     , l.site_id
     , s.url as site_url
     , i.page_id
     , p.path as page_path
     , lemma_rank as count_per_page
     , l.frequency as count_per_site
from search_index i
         join lemma l on i.lemma_id = l.id
         join search_engine.site s on s.id = l.site_id
         join search_engine.page p on l.site_id = p.site_id and p.id = i.page_id
where l.lemma = :lemmaName