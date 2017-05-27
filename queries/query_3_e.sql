-- top 10 publisher id
SELECT publisher_id
FROM series
GROUP BY publisher_id
ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY;

-- Assign rank to each language id for each publisher id in top 10
SELECT series.publisher_id,
       series.language_id,
       ROW_NUMBER() OVER (PARTITION BY series.publisher_id
                          ORDER BY COUNT(series.language_id) DESC) AS rank
FROM
  (SELECT publisher_id
   FROM series
   GROUP BY publisher_id
   ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY) useful_publishers
INNER JOIN series ON useful_publishers.publisher_id = series.publisher_id
GROUP BY series.publisher_id,
         series.language_id;

-- answer but ids instead of names
SELECT publisher_id,
       language_id
FROM
  (SELECT series.publisher_id,
          series.language_id,
          ROW_NUMBER() OVER (PARTITION BY series.publisher_id
                             ORDER BY COUNT(series.language_id) DESC) AS rank
   FROM
     (SELECT publisher_id
      FROM series
      GROUP BY publisher_id
      ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY) useful_publishers
   INNER JOIN series ON useful_publishers.publisher_id = series.publisher_id
   GROUP BY series.publisher_id,
            series.language_id)
WHERE rank <= 3
ORDER BY publisher_id,
         rank;

-- answer
SELECT publisher.name,
       language.name
FROM
  (SELECT publisher_id,
          language_id
   FROM
     (SELECT series.publisher_id,
             series.language_id,
             ROW_NUMBER() OVER (PARTITION BY series.publisher_id
                                ORDER BY COUNT(series.language_id) DESC) AS rank
      FROM
        (SELECT publisher_id
         FROM series
         GROUP BY publisher_id
         ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY) useful_publishers
      INNER JOIN series ON useful_publishers.publisher_id = series.publisher_id
      GROUP BY series.publisher_id,
               series.language_id)
   WHERE rank <= 3
   ORDER BY publisher_id,
            rank) res
INNER JOIN publisher ON publisher.id = res.publisher_id
INNER JOIN LANGUAGE ON language.id = res.language_id;