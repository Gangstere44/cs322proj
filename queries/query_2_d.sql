SELECT I.publication_date, COUNT(I.num) 
FROM issue I  
WHERE I.publication_date >= 1990 
GROUP BY I.publication_date
