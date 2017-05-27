-- indicia publisher and brand group ids having the same publisher
-- DISTINCT is needed because it is possible that there are some brand groups
-- having the same name and being linked to the same publisher id
SELECT DISTINCT indicia_publisher.id AS indicica_publisher_id, brand_group.name AS brand_group_name
FROM indicia_publisher
INNER JOIN brand_group ON indicia_publisher.publisher_id = brand_group.publisher_id;

-- answer
SELECT brand_group_name, COUNT(*) AS nb_indicia_publisher
FROM
  (SELECT DISTINCT indicia_publisher.id AS indicica_publisher_id, brand_group.name AS brand_group_name
  FROM indicia_publisher
  INNER JOIN brand_group ON indicia_publisher.publisher_id = brand_group.publisher_id)
GROUP BY brand_group_name
ORDER BY nb_indicia_publisher DESC
FETCH FIRST 10 ROWS ONLY;