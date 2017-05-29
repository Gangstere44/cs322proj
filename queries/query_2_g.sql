SELECT DISTINCT A.name 
FROM Story_to_Script STS, Story_to_Pencils STP, Story_to_Colors STC, Artist A
WHERE STS.artist_id = STP.artist_id AND STS.artist_id = STC.artist_id AND A.id = STS.artist_id;