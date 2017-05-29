-- !!! Not answering the query !!!

SELECT * 
FROM
    (SELECT COUNT(SR.target_id)
    FROM story S, story_reprint SR 
    WHERE s.ID IN SR.origin_id 
    GROUP BY SR.origin_id
    ORDER BY 1 DESC
    )
WHERE ROWNUM <= 10;

-- Valentin's suggestion:

-- answer (but still having the 'NULL' title)
SELECT title 
FROM
    (SELECT SR.origin_id, COUNT(SR.target_id)
    FROM story_reprint SR  
    GROUP BY SR.origin_id
    ORDER BY 2 DESC
    FETCH FIRST 10 ROWS ONLY
    )
INNER JOIN story ON story.id = origin_id;
