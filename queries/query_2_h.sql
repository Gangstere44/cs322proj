-- !!! Taking infinite amount of time !!!

SELECT S.title
FROM story S, story_to_characters STC, story_reprint SR, characters C, story_to_feature STF 
WHERE (S.id NOT IN SR.origin_id) AND C.name = 'Batman' AND C.id = STC.character_id AND (C.id NOT IN STF.character_id);

-- Valentin's suggestion:

-- Batman id(s)
SELECT id
FROM characters
WHERE UPPER(name) = UPPER('batman');

-- reprinted stories ids
SELECT DISTINCT origin_id
FROM story_reprint;

-- non-reprinted stories ids
SELECT id
FROM story
WHERE id NOT IN
  (SELECT DISTINCT origin_id
  FROM story_reprint);
  
-- all stories ids with batman as a character
SELECT NR_stories.id
FROM 
  (SELECT id
  FROM story
  WHERE id NOT IN
    (SELECT DISTINCT origin_id
    FROM story_reprint)) NR_stories
INNER JOIN story_to_characters ON story_to_characters.story_id = NR_stories.id AND character_id IN
  (SELECT id
  FROM characters
  WHERE UPPER(name) = UPPER('batman'));
  
-- all stories ids with batman as a featured character
SELECT NR_stories.id
FROM 
  (SELECT id
  FROM story
  WHERE id NOT IN
    (SELECT DISTINCT origin_id
    FROM story_reprint)) NR_stories
INNER JOIN story_to_feature ON story_to_feature.story_id = NR_stories.id AND character_id IN
  (SELECT id
  FROM characters
  WHERE UPPER(name) = UPPER('batman'));
  
-- answer (with title only) -> one minus the other
-- distinct because some id has the same title
SELECT DISTINCT title
FROM story
WHERE id IN
    (SELECT NR_stories.id
     FROM
       (SELECT id
        FROM story
        WHERE id NOT IN
            (SELECT DISTINCT origin_id
             FROM story_reprint)) NR_stories
     INNER JOIN story_to_characters ON story_to_characters.story_id = NR_stories.id
     AND character_id IN
       (SELECT id
        FROM characters
        WHERE UPPER(name) = UPPER('batman')) MINUS SELECT NR_stories.id
     FROM
       (SELECT id
        FROM story
        WHERE id NOT IN
            (SELECT DISTINCT origin_id
             FROM story_reprint)) NR_stories
     INNER JOIN story_to_feature ON story_to_feature.story_id = NR_stories.id
     AND character_id IN
       (SELECT id
        FROM characters
        WHERE UPPER(name) = UPPER('batman')));