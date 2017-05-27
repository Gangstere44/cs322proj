-- every story id with its number of writers
SELECT story_id, COUNT(*) AS nb_writers
FROM story_to_script
GROUP BY story_id;

-- answer with ids
SELECT issue.indicia_publisher_id
FROM story
INNER JOIN 
  (SELECT story_id, COUNT(*) AS nb_writers
  FROM story_to_script
  GROUP BY story_id) story_to_nb_writers ON story_to_nb_writers.story_id = story.id
INNER JOIN issue ON issue.id = story.issue_id AND issue.indicia_publisher_id IS NOT NULL
GROUP BY issue.indicia_publisher_id
ORDER BY MAX(story_to_nb_writers.nb_writers) DESC
FETCH FIRST 10 ROWS ONLY;

-- answer (3.5 sec!)
SELECT name
FROM indicia_publisher
WHERE id IN (SELECT issue.indicia_publisher_id
  FROM story
  INNER JOIN 
    (SELECT story_id, COUNT(*) AS nb_writers
    FROM story_to_script
    GROUP BY story_id) story_to_nb_writers ON story_to_nb_writers.story_id = story.id
  INNER JOIN issue ON issue.id = story.issue_id AND issue.indicia_publisher_id IS NOT NULL
  GROUP BY issue.indicia_publisher_id
  ORDER BY MAX(story_to_nb_writers.nb_writers) DESC
  FETCH FIRST 10 ROWS ONLY);