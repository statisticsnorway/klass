-- renames a foreign key column

ALTER TABLE level
  DROP CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4;

ALTER TABLE level ALTER COLUMN statistical_classification RENAME TO statistical_classification_id;

ALTER TABLE level ALTER COLUMN statistical_classification_id BIGINT not null;

ALTER TABLE level
  ADD CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4 FOREIGN KEY (
  statistical_classification_id) REFERENCES statistical_classification (id);

