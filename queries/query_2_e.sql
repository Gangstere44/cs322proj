SELECT COUNT(s.publisher_id) 
FROM indicia_publisher IP, series S 
WHERE IP.name LIKE '%DC Comics%' AND S.publisher_id = IP.publisher_id 
GROUP BY s.publisher_id
