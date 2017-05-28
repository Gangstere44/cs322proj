SELECT * 
FROM
    (SELECT COUNT(SR.target_id)
    FROM story S, story_reprint SR 
    WHERE s.ID IN SR.origin_id 
    GROUP BY SR.origin_id
    ORDER BY 1 DESC
    )
WHERE ROWNUM <= 10