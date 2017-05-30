-- magazine id
SELECT id
FROM series_publication_type
WHERE name = 'magazine';

CREATE INDEX ISSUE_ID_ON_STORY ON story (issue_id ASC);

-- italy id
SELECT id
FROM country
WHERE UPPER(name) = UPPER('italy');

-- all italian magazine series ids
SELECT id
FROM series
WHERE publication_type_id =
    (SELECT id
     FROM series_publication_type
     WHERE UPPER(name) = UPPER('magazine'))
AND country_id =
    (SELECT id
    FROM country
    WHERE UPPER(name) = UPPER('italy'));
     
-- all ids of issues appearing in an italian magazine
SELECT issue_id
FROM issue_in_series
INNER JOIN
  (SELECT id
  FROM series
  WHERE publication_type_id =
      (SELECT id
       FROM series_publication_type
       WHERE UPPER(name) = UPPER('magazine'))
  AND country_id =
      (SELECT id
      FROM country
      WHERE UPPER(name) = UPPER('italy'))) useful_series ON useful_series.id = issue_in_series.series_id;
      
-- all story types of stories appearing in italian magazines
SELECT DISTINCT type_id FROM story INNER JOIN (SELECT issue_id
FROM issue_in_series
INNER JOIN
  (SELECT id
  FROM series
  WHERE publication_type_id =
      (SELECT id
       FROM series_publication_type
       WHERE UPPER(name) = UPPER('magazine'))
  AND country_id =
      (SELECT id
      FROM country
      WHERE UPPER(name) = UPPER('italy'))) useful_series ON useful_series.id = issue_in_series.series_id) useful_issues ON useful_issues.issue_id = story.issue_id;

-- answer but way slower than the next one (why?)
SELECT name
FROM story_type
WHERE id NOT IN
  (SELECT DISTINCT type_id
   FROM story
   INNER JOIN
     (SELECT issue_id
      FROM issue_in_series
      INNER JOIN
        (SELECT id
         FROM series
         WHERE publication_type_id =
             (SELECT id
              FROM series_publication_type
              WHERE UPPER(name) = UPPER('magazine'))
           AND country_id =
             (SELECT id
              FROM country
              WHERE UPPER(name) = UPPER('italy'))) useful_series ON useful_series.id = issue_in_series.series_id) useful_issues ON useful_issues.issue_id = story.issue_id);      

-- true answer (faster)
SELECT name
FROM story_type
LEFT JOIN
  (SELECT DISTINCT type_id
   FROM story
   INNER JOIN
     (SELECT issue_id
      FROM issue_in_series
      INNER JOIN
        (SELECT id
         FROM series
         WHERE publication_type_id =
             (SELECT id
              FROM series_publication_type
              WHERE UPPER(name) = UPPER('magazine'))
           AND country_id =
             (SELECT id
              FROM country
              WHERE UPPER(name) = UPPER('italy'))) useful_series ON useful_series.id = issue_in_series.series_id) useful_issues ON useful_issues.issue_id = story.issue_id) italian_story_types ON italian_story_types.type_id = story_type.id
WHERE italian_story_types.type_id IS NULL; 

