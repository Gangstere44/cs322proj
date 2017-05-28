SELECT S.title
FROM story S, story_to_characters STC, story_reprint SR, characters C, story_to_feature STF 
WHERE (S.id NOT IN SR.origin_id) AND C.name = 'Batman' AND C.id = STC.character_id AND (C.id NOT IN STF.character_id)