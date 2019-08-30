-- Migrating classifications from Stabas requires some increased column sizes
ALTER TABLE classification_item MODIFY notes VARCHAR(6000);

ALTER TABLE statistical_classification MODIFY introduction VARCHAR(7168) not null;
ALTER TABLE statistical_classification MODIFY legal_base VARCHAR(4000);
ALTER TABLE statistical_classification MODIFY publications VARCHAR(4000);
ALTER TABLE statistical_classification MODIFY derived_from VARCHAR(4000);

ALTER TABLE correspondence_table MODIFY description VARCHAR(4000) not null;

ALTER TABLE level MODIFY name VARCHAR(1000) not null;
