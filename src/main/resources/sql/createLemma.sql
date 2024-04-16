insert into lemma (site_id, lemma, frequency)
values (:siteId, :lemma, :frequency)
ON DUPLICATE KEY UPDATE frequency = frequency + :frequency