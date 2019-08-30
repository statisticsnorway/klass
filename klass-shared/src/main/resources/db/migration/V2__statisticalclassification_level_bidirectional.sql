-- renames a foreign key column

ALTER TABLE level
  DROP FOREIGN KEY fk_sxgu9ljubog3jnujn8fvohvn4;

ALTER TABLE level CHANGE statistical_classification statistical_classification_id BIGINT not null;

ALTER TABLE level
  ADD CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4 FOREIGN KEY (
  statistical_classification_id) REFERENCES statistical_classification (id);

