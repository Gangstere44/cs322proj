-- Index on type_id in story was helping a little bit: 1.8 sec instead of 2.0 sec

-- most freq type id
SELECT type_id
FROM story
GROUP BY type_id
ORDER BY COUNT(type_id) DESC FETCH FIRST 1 ROWS ONLY;

-- issue ids of stories not with type_id = most_freq_type_id
SELECT DISTINCT issue_id
FROM story
WHERE type_id NOT IN
    (SELECT type_id
     FROM story
     GROUP BY type_id
     ORDER BY COUNT(type_id) DESC FETCH FIRST 1 ROWS ONLY);

-- all series_id with the most issues which contain a story that is not of the most frequent type in the DB
SELECT series_id
FROM issue_in_series
WHERE issue_id IN
    (SELECT DISTINCT issue_id
     FROM story
     WHERE type_id NOT IN
         (SELECT type_id
          FROM story
          GROUP BY type_id
          ORDER BY COUNT(type_id) DESC FETCH FIRST 1 ROWS ONLY))
GROUP BY series_id
ORDER BY COUNT(issue_id) DESC FETCH FIRST 1 ROWS WITH TIES;

-- answer
SELECT name
FROM series
WHERE id IN
    (SELECT series_id
     FROM issue_in_series
     WHERE issue_id IN
         (SELECT DISTINCT issue_id
          FROM story
          WHERE type_id NOT IN
              (SELECT type_id
               FROM story
               GROUP BY type_id
               ORDER BY COUNT(type_id) DESC FETCH FIRST 1 ROWS ONLY))
     GROUP BY series_id
     ORDER BY COUNT(issue_id) DESC FETCH FIRST 1 ROWS WITH TIES);