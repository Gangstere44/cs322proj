-- all publisher_ids that have published all types of series
SELECT publisher_id
FROM series
WHERE publication_type_id IS NOT NULL
GROUP BY publisher_id
HAVING COUNT(DISTINCT publication_type_id) =
  (SELECT COUNT(*)
   FROM series_publication_type);

-- answer
SELECT name
FROM publisher
WHERE id IN
    (SELECT publisher_id
     FROM series
     WHERE publication_type_id IS NOT NULL
     GROUP BY publisher_id
     HAVING COUNT(DISTINCT publication_type_id) =
       (SELECT COUNT(*)
        FROM series_publication_type));