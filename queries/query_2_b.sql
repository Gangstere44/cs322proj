-- !!! Forgot DISTINCT + use of LIKE useless (+ use of UPPER to ease integration, but that's ok) !!!

SELECT P.id, P.name  
FROM publisher P, country C, series s, series_publication_type SPT 
WHERE c.name = 'Denmark' AND c.id = S.country_id AND P.id = S.publisher_id AND s.PUBLICATION_TYPE_ID = SPT.id AND SPT.name LIKE '%book%';

-- Valentin's suggestion:

-- easy answer
SELECT DISTINCT P.id, P.name  
FROM publisher P, country C, series s, series_publication_type SPT 
WHERE UPPER(c.name) = UPPER('denmark') AND c.id = S.country_id AND P.id = S.publisher_id AND s.PUBLICATION_TYPE_ID = SPT.id AND UPPER(SPT.name) = UPPER('book');

-- OR

-- Denmark id
SELECT id
FROM country
WHERE UPPER(name) = UPPER('denmark');

-- Book id
SELECT id
FROM series_publication_type
WHERE UPPER(name) = UPPER('book');

-- Danish book series publisher ids
SELECT DISTINCT publisher_id
FROM series
WHERE country_id =
  (SELECT id
  FROM country
  WHERE UPPER(name) = UPPER('denmark'))
AND publication_type_id =
  (SELECT id
  FROM series_publication_type
  WHERE UPPER(name) = UPPER('book'));
  
-- answer
SELECT id, name
FROM publisher
INNER JOIN
  (SELECT DISTINCT publisher_id
  FROM series
  WHERE country_id =
    (SELECT id
    FROM country
    WHERE UPPER(name) = UPPER('denmark'))
  AND publication_type_id =
    (SELECT id
    FROM series_publication_type
    WHERE UPPER(name) = UPPER('book'))) ON publisher_id = publisher.id;
