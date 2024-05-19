delete s from search_index s
    join page p on s.page_id = p.id where p.site_id = :siteId