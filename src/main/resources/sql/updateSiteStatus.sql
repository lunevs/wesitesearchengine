update site
set status_time = current_timestamp, status = :statusName, last_error = :lastError
where id = :siteId