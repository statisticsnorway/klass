ALTER TABLE classification_series ADD hierarchy_last_modified DATETIME NULL;
UPDATE classification_series SET hierarchy_last_modified = last_modified;
ALTER TABLE classification_series MODIFY hierarchy_last_modified DATETIME NOT NULL;