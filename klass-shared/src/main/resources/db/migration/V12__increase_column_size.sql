-- Migrating classifications from Stabas requires some increased column sizes
ALTER TABLE classification_item MODIFY official_name VARCHAR(2048);
