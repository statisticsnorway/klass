-- Only classes(tables) implementing SoftDeletable shall have deleted column
 
ALTER TABLE classification_family DROP COLUMN deleted;
ALTER TABLE classification_item DROP COLUMN deleted;
ALTER TABLE correspondence_map DROP COLUMN deleted;
ALTER TABLE level DROP COLUMN deleted;
ALTER TABLE statistical_unit DROP COLUMN deleted;
ALTER TABLE subscriber DROP COLUMN deleted;
ALTER TABLE subscription DROP COLUMN deleted;
ALTER TABLE user DROP COLUMN deleted;
