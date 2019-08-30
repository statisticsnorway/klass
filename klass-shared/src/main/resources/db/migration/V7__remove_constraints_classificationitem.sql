-- Removed not null constraint since a ReferencingClassificationItem does not have these set
ALTER TABLE classification_item MODIFY code VARCHAR(255);
ALTER TABLE classification_item MODIFY official_name VARCHAR(1024);
