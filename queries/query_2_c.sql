SELECT S.name 
FROM series S, series_publication_type SPT, country C 
WHERE S.country_id = C.id AND C.name = 'Switzerland' AND SPT.id = S.publication_type_id AND SPT.name = 'magazine';

-- Valentin's suggestion

-- answer
SELECT S.name 
FROM series S, series_publication_type SPT, country C 
WHERE S.country_id = C.id AND UPPER(C.name) = UPPER('switzerland') AND SPT.id = S.publication_type_id AND UPPER(SPT.name) = UPPER('magazine');