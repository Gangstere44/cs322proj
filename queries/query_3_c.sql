 -- artist id of Alan Moore
SELECT id
FROM artist
WHERE UPPER(name) = UPPER('Alan Moore');

-- all story_ids of stories written by Alan Moore
SELECT story_id
FROM story_to_script
WHERE artist_id =
    (SELECT id
     FROM artist
     WHERE UPPER(name) = UPPER('Alan Moore'));

-- all reprinted story_ids of Alan Moore
SELECT target_id
FROM story_reprint
WHERE target_id IN
    (SELECT story_id
     FROM story_to_script
     WHERE artist_id =
         (SELECT id
          FROM artist
          WHERE UPPER(name) = UPPER('Alan Moore')));

-- top 10 character ids in reprinted stories of Alan Moore
SELECT character_id
FROM story_to_characters
WHERE story_id IN
    (SELECT target_id
     FROM story_reprint
     WHERE target_id IN
         (SELECT story_id
          FROM story_to_script
          WHERE artist_id =
              (SELECT id
               FROM artist
               WHERE UPPER(name) = UPPER('Alan Moore'))))
GROUP BY character_id
ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY;

-- answer
SELECT name
FROM characters
WHERE id IN
    (SELECT character_id
     FROM story_to_characters
     WHERE story_id IN
         (SELECT target_id
          FROM story_reprint
          WHERE target_id IN
              (SELECT story_id
               FROM story_to_script
               WHERE artist_id =
                   (SELECT id
                    FROM artist
                    WHERE UPPER(name) = UPPER('Alan Moore'))))
     GROUP BY character_id
     ORDER BY COUNT(*) DESC FETCH FIRST 10 ROWS ONLY);