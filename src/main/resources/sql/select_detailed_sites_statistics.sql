select s.name, s.url, s.status, UNIX_TIMESTAMP(s.status_time)*1000 as status_time, s.last_error as error, p.pages, l.lemmas
from site s
join (select site_id, count(*) as pages from page group by site_id) p on s.id = p.site_id
join (select site_id, count(*) as lemmas from lemma group by site_id) l on s.id = l.site_id;
