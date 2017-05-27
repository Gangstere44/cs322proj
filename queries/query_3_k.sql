-- single-issue series ids
SELECT series_id
FROM issue_in_series
GROUP BY series_id
HAVING COUNT(*) = 1;

-- answer
SELECT indicia_publisher.name,
       COUNT(*)
FROM series
INNER JOIN
  ( SELECT series_id
   FROM issue_in_series
   GROUP BY series_id
   HAVING COUNT(*) = 1) single_issue_series ON single_issue_series.series_id = series.id
INNER JOIN indicia_publisher ON indicia_publisher.publisher_id = series.publisher_id
GROUP BY indicia_publisher.name
ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY;