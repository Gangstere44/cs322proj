SELECT MAX(COUNT(BG.name)) 
FROM indicia_publisher IP, publisher P, brand_group BG, country C  
WHERE C.name = 'Belgium' AND C.id = IP.country_id AND BG.publisher_id = IP.publisher_id 
GROUP BY IP.name