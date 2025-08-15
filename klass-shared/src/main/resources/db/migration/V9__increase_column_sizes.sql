-- Migrating classifications from Stabas requires some increased column sizes
ALTER TABLE classification_item ALTER COLUMN notes VARCHAR(6000);

ALTER TABLE statistical_classification ALTER COLUMN introduction VARCHAR(7168) not null;
ALTER TABLE statistical_classification ALTER COLUMN legal_base VARCHAR(4000);
ALTER TABLE statistical_classification ALTER COLUMN publications VARCHAR(4000);
ALTER TABLE statistical_classification ALTER COLUMN derived_from VARCHAR(4000);

ALTER TABLE correspondence_table ALTER COLUMN description VARCHAR(4000) not null;

ALTER TABLE level ALTER COLUMN name VARCHAR(1000) not null;
