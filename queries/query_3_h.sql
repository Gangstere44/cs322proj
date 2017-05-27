SELECT id FROM story_type WHERE UPPER(name) = UPPER('cartoon'); -- cartoon id

SELECT id FROM story WHERE type_id = (SELECT id FROM story_type WHERE UPPER(name) = UPPER('cartoon')); -- all cartoons

SELECT issue_id FROM story WHERE type_id = (SELECT id FROM story_type WHERE UPPER(name) = UPPER('cartoon')); -- issues ids of all cartoons

-- all artist ids and ids of indicia publishers which they have already worked for
SELECT DISTINCT story_to_script.artist_id, issue.indicia_publisher_id
FROM
  (SELECT id, issue_id 
  FROM story 
  WHERE type_id =
    (SELECT id 
    FROM story_type 
    WHERE UPPER(name) = UPPER('cartoon'))) cartoons 
  INNER JOIN issue ON issue.id = cartoons.issue_id AND issue.indicia_publisher_id IS NOT NULL
  INNER JOIN story_to_script ON story_to_script.story_id = cartoons.id;
  
-- answer
SELECT artist_id, COUNT(*) AS nb_indicia_publishers
FROM
  (SELECT DISTINCT story_to_script.artist_id, issue.indicia_publisher_id
  FROM
    (SELECT id, issue_id 
    FROM story 
    WHERE type_id =
      (SELECT id 
      FROM story_type 
      WHERE UPPER(name) = UPPER('cartoon'))) cartoons 
    INNER JOIN issue ON issue.id = cartoons.issue_id AND issue.indicia_publisher_id IS NOT NULL
    INNER JOIN story_to_script ON story_to_script.story_id = cartoons.id)
  GROUP BY artist_id
  HAVING COUNT(*) > 1;
