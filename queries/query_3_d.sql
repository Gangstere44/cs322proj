-- nature-related genre ids
SELECT id
FROM genre
WHERE UPPER(name) = UPPER('nature');

-- all nature-related story ids
SELECT count(DISTINCT story_id)
FROM story_to_genre
WHERE genre_id IN
    (SELECT id
     FROM genre
     WHERE UPPER(name) = UPPER('nature'));

-- all writer ids for each nature-related stories and get NULL as an 'artist-pencil' if not the same than the writer, otherwise get the same id
SELECT story_to_script.artist_id,
       story_to_pencils.artist_id AS artist_id_pencil
FROM
  (SELECT DISTINCT story_id
   FROM story_to_genre
   WHERE genre_id IN
       (SELECT id
        FROM genre
        WHERE UPPER(name) = UPPER('nature'))) useful_stories
INNER JOIN story_to_script ON story_to_script.story_id = useful_stories.story_id
LEFT JOIN story_to_pencils ON story_to_pencils.story_id = useful_stories.story_id
AND story_to_pencils.artist_id = story_to_script.artist_id
GROUP BY story_to_script.artist_id,
         story_to_pencils.artist_id,
         story_to_script.story_id;

-- answer with ids
SELECT artist_id
FROM
  (SELECT story_to_script.artist_id,
          story_to_pencils.artist_id AS artist_id_pencil
   FROM
     (SELECT DISTINCT story_id
      FROM story_to_genre
      WHERE genre_id IN
          (SELECT id
           FROM genre
           WHERE UPPER(name) = UPPER('nature'))) useful_stories
   INNER JOIN story_to_script ON story_to_script.story_id = useful_stories.story_id
   LEFT JOIN story_to_pencils ON story_to_pencils.story_id = useful_stories.story_id
   AND story_to_pencils.artist_id = story_to_script.artist_id
   GROUP BY story_to_script.artist_id,
            story_to_pencils.artist_id,
            story_to_script.story_id) remaining_artists
GROUP BY artist_id
HAVING COUNT(artist_id_pencil) = COUNT(*);

-- answer
SELECT name
FROM artist
WHERE id IN
    (SELECT artist_id
     FROM
       (SELECT story_to_script.artist_id,
               story_to_pencils.artist_id AS artist_id_pencil
        FROM
          (SELECT DISTINCT story_id
           FROM story_to_genre
           WHERE genre_id IN
               (SELECT id
                FROM genre
                WHERE UPPER(name) = UPPER('nature'))) useful_stories
        INNER JOIN story_to_script ON story_to_script.story_id = useful_stories.story_id
        LEFT JOIN story_to_pencils ON story_to_pencils.story_id = useful_stories.story_id
        AND story_to_pencils.artist_id = story_to_script.artist_id
        GROUP BY story_to_script.artist_id,
                 story_to_pencils.artist_id,
                 story_to_script.story_id) remaining_artists
     GROUP BY artist_id
     HAVING COUNT(artist_id_pencil) = COUNT(*));