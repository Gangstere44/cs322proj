-- each indicia publisher with the length of the series they published
SELECT indicia_publisher.name,
       (series.publication_dates_to_year - series.publication_dates_from_year) AS series_length
FROM series
INNER JOIN indicia_publisher ON series.publisher_id = indicia_publisher.publisher_id
AND series.publication_dates_to_year IS NOT NULL
AND series.publication_dates_from_year IS NOT NULL;

-- answer
SELECT indicia_series_length.name,
       AVG(indicia_series_length.series_length) AS average_series_length
FROM
  (SELECT indicia_publisher.name,
          (series.publication_dates_to_year - series.publication_dates_from_year) AS series_length
   FROM series
   INNER JOIN indicia_publisher ON series.publisher_id = indicia_publisher.publisher_id
   AND series.publication_dates_to_year IS NOT NULL
   AND series.publication_dates_from_year IS NOT NULL) indicia_series_length
GROUP BY indicia_series_length.name;
