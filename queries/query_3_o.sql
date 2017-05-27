-- answer with id + title
SELECT origin_id, story.title
FROM story
INNER JOIN story_reprint ON story.id = story_reprint.origin_id AND story.issue_id = 123
GROUP BY origin_id, story.title
ORDER BY COUNT(*) DESC
FETCH FIRST 1 ROWS ONLY;

-- answer (only title)
SELECT title
FROM 
  (SELECT origin_id, story.title
  FROM story
  INNER JOIN story_reprint ON story.id = story_reprint.origin_id AND story.issue_id = 123
  GROUP BY origin_id, story.title
  ORDER BY COUNT(*) DESC
  FETCH FIRST 1 ROWS ONLY);