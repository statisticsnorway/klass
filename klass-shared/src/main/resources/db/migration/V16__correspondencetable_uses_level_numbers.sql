-- CorrespondenceTables stores level number instead of a foreign key to level
ALTER TABLE correspondence_table DROP CONSTRAINT fk_cqdqwek3akelq2qqnhuydrkqr;
ALTER TABLE correspondence_table DROP CONSTRAINT fk_8bn11tm1gsou84v8kmoug6qyt;

UPDATE correspondence_table ct SET ct.source_level_id = 
    COALESCE((SELECT l.level_number FROM level l WHERE l.id = ct.source_level_id), 0);

UPDATE correspondence_table ct SET ct.target_level_id = 
    COALESCE((SELECT l.level_number FROM level l WHERE l.id = ct.target_level_id), 0);

ALTER TABLE correspondence_table ALTER COLUMN source_level_id RENAME TO source_level_number;
ALTER TABLE correspondence_table ALTER COLUMN target_level_id RENAME TO target_level_number;

ALTER TABLE correspondence_table ALTER COLUMN source_level_number INTEGER NOT NULL;
ALTER TABLE correspondence_table ALTER COLUMN target_level_number INTEGER NOT NULL;
