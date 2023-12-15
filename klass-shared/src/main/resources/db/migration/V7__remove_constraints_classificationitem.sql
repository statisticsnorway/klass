-- Removed not null constraint since a ReferencingClassificationItem does not have these set
ALTER TABLE classification_item ALTER COLUMN code VARCHAR(255);
ALTER TABLE classification_item ALTER COLUMN official_name VARCHAR(1024);
