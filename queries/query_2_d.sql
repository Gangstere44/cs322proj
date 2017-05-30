-- !!! Putting some meaningless results (not very important, but little improvement possible) !!!

SELECT I.publication_date, COUNT(I.num) 
FROM issue I  
WHERE I.publication_date >= 1990 
GROUP BY I.publication_date;

CREATE INDEX INDEX1 ON issue (publication_date ASC);

-- Valentin's suggestion:
SELECT I.publication_date,
       COUNT(I.num) AS nb_issues
FROM issue I
WHERE I.publication_date >= 1990
  AND I.publication_date <= 2017
GROUP BY I.publication_date
ORDER BY I.publication_date;