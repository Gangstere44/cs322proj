-- answer (0.8 sec, seems it can be faster with index on series.name??)
SELECT name
FROM issue_in_series
INNER JOIN series ON series.id = issue_in_series.series_id
GROUP BY name
ORDER BY COUNT(*) DESC
FETCH FIRST 5 ROWS ONLY;

-- answer (0.4 sec, seems quite fast, but cost of plan is veeerrryyy high, why?)
SELECT 
  (SELECT name
  FROM series
  WHERE series.id = issue_in_series.series_id)
FROM issue_in_series
GROUP BY series_id
ORDER BY COUNT(*) DESC
FETCH FIRST 5 ROWS ONLY;