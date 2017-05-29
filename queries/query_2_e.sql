-- !!! Why grouping by publisher ids? Also need name (or ID maybe?) with the count !!!

SELECT COUNT(s.publisher_id) 
FROM indicia_publisher IP, series S 
WHERE IP.name LIKE '%DC Comics%' AND S.publisher_id = IP.publisher_id 
GROUP BY s.publisher_id;

-- Valentin's suggestion

-- indicia publishers names + publisher ids we want
SELECT name, publisher_id
FROM indicia_publisher
WHERE UPPER(name) LIKE UPPER('%dc comics%');

-- answer
SELECT IP.name, COUNT(*) AS nb_series
FROM series
INNER JOIN
  (SELECT name, publisher_id
  FROM indicia_publisher
  WHERE UPPER(name) LIKE UPPER('%dc comics%')) IP ON IP.publisher_id = series.publisher_id
GROUP BY IP.name;
