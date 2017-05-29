-- !!! Not answering the query !!!

SELECT MAX(COUNT(BG.name)) 
FROM indicia_publisher IP, publisher P, brand_group BG, country C  
WHERE C.name = 'Belgium' AND C.id = IP.country_id AND BG.publisher_id = IP.publisher_id 
GROUP BY IP.name;

-- VALENTIN's suggestion:

-- Belgium id
SELECT id
FROM country
WHERE UPPER(name) = UPPER('belgium');

-- all belgian indicia-publisher publisher ids
SELECT publisher_id
FROM indicia_publisher
WHERE country_id = 
  (SELECT id
  FROM country
  WHERE UPPER(name) = UPPER('belgium'));
  
-- answer with id
SELECT brand_group.id, COUNT(*)
FROM brand_group
INNER JOIN 
  (SELECT publisher_id
  FROM indicia_publisher
  WHERE country_id = 
    (SELECT id
    FROM country
    WHERE UPPER(name) = UPPER('belgium'))) useful_IP ON useful_IP.publisher_id = brand_group.publisher_id
GROUP BY brand_group.id
ORDER BY COUNT(*) DESC
FETCH FIRST 1 ROWS WITH TIES;

-- answer
SELECT name
FROM brand_group
WHERE id IN
  (SELECT brand_group.id
  FROM brand_group
  INNER JOIN 
    (SELECT publisher_id
    FROM indicia_publisher
    WHERE country_id = 
      (SELECT id
      FROM country
      WHERE UPPER(name) = UPPER('belgium'))) useful_IP ON useful_IP.publisher_id = brand_group.publisher_id
  GROUP BY brand_group.id
  ORDER BY COUNT(*) DESC
  FETCH FIRST 1 ROWS WITH TIES);