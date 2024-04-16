select l.id, l.site_id, l.lemma, l.frequency
from lemma as l
where l.site_id = :siteId