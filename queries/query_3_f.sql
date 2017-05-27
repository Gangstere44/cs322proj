-- all reprinted stories
SELECT target_id
FROM story_reprint;

-- magazine id
SELECT id
FROM series_publication_type
WHERE UPPER(name) = UPPER('magazine');

-- all magazine series ids, with correpsonding language id
SELECT id,
       language_id
FROM series
WHERE publication_type_id =
    (SELECT id
     FROM series_publication_type
     WHERE UPPER(name) = UPPER('magazine'));

-- all ids of issues appearing in a magazine, with correpsonding language id
SELECT issue_id,
       useful_series.language_id
FROM issue_in_series
INNER JOIN
  (SELECT id,
          language_id
   FROM series
   WHERE publication_type_id =
       (SELECT id
        FROM series_publication_type
        WHERE UPPER(name) = UPPER('magazine'))) useful_series ON useful_series.id = issue_in_series.series_id;

-- all ids of original stories that appeared in a magazine, with correpsonding language id
SELECT id,
       useful_issues.language_id
FROM story
INNER JOIN
  (SELECT issue_id,
          useful_series.language_id
   FROM issue_in_series
   INNER JOIN
     (SELECT id,
             language_id
      FROM series
      WHERE publication_type_id =
          (SELECT id
           FROM series_publication_type
           WHERE UPPER(name) = UPPER('magazine'))) useful_series ON useful_series.id = issue_in_series.series_id) useful_issues ON useful_issues.issue_id = story.issue_id
WHERE id NOT IN
    (SELECT target_id
     FROM story_reprint);

-- answer
SELECT name,
       nb_orig_stories_magazine
FROM
  (SELECT useful_issues.language_id,
          COUNT(*) AS nb_orig_stories_magazine
   FROM story
   INNER JOIN
     (SELECT issue_id,
             useful_series.language_id
      FROM issue_in_series
      INNER JOIN
        (SELECT id,
                language_id
         FROM series
         WHERE publication_type_id =
             (SELECT id
              FROM series_publication_type
              WHERE UPPER(name) = UPPER('magazine'))) useful_series ON useful_series.id = issue_in_series.series_id) useful_issues ON useful_issues.issue_id = story.issue_id
   WHERE id NOT IN
       (SELECT target_id
        FROM story_reprint)
   GROUP BY language_id
   HAVING COUNT(*) > 10000)
INNER JOIN LANGUAGE ON id = language_id;
