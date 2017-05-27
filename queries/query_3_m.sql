-- ids of publishers of crossovers
SELECT id
FROM publisher
WHERE UPPER(name) = UPPER('DC / Marvel') OR UPPER(name) = UPPER('Marvel / DC');

-- id of Marvel publisher
SELECT id
FROM publisher
WHERE UPPER(name) = UPPER('Marvel');

-- ids of indicia publishers that published story crossovers
SELECT id
FROM indicia_publisher
WHERE publisher_id IN
  (SELECT id
  FROM publisher
  WHERE UPPER(name) = UPPER('DC / Marvel') OR UPPER(name) = UPPER('Marvel / DC'));
  
-- ids of Marvel indicia publishers
SELECT id
FROM indicia_publisher
WHERE publisher_id IN
  (SELECT id
  FROM publisher
  WHERE UPPER(name) = UPPER('Marvel'));

-- answer
SELECT DISTINCT characters.name
FROM issue
INNER JOIN (
  SELECT id
  FROM indicia_publisher
  WHERE publisher_id IN
    (SELECT id
    FROM publisher
    WHERE UPPER(name) = UPPER('DC / Marvel') OR UPPER(name) = UPPER('Marvel / DC'))) indicia_crossover ON indicia_crossover.id = issue.indicia_publisher_id
INNER JOIN story ON story.issue_id = issue.id
INNER JOIN story_to_characters ON story.id = story_to_characters.story_id
INNER JOIN characters ON characters.id = story_to_characters.character_id
INTERSECT
SELECT DISTINCT characters.name
FROM issue
INNER JOIN (
  SELECT id
  FROM indicia_publisher
  WHERE publisher_id IN
    (SELECT id
    FROM publisher
    WHERE UPPER(name) = UPPER('Marvel'))) indicia_crossover ON indicia_crossover.id = issue.indicia_publisher_id
INNER JOIN story ON story.issue_id = issue.id
INNER JOIN story_to_characters ON story.id = story_to_characters.story_id
INNER JOIN characters ON characters.id = story_to_characters.character_id;