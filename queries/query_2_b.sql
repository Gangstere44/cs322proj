SELECT P.id, P.name  
FROM publisher P, country C, series s, series_publication_type SPT 
WHERE c.name = 'Denmark' AND c.id = S.country_id AND P.id = S.publisher_id AND s.PUBLICATION_TYPE_ID = SPT.id AND SPT.name LIKE '%book%'