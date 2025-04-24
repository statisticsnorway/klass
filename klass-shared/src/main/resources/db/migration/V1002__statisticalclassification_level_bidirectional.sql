-- renames a foreign key column

ALTER TABLE level
    DROP CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4;

ALTER TABLE level
    RENAME COLUMN statistical_classification TO statistical_classification_id;

ALTER TABLE level
    ALTER COLUMN statistical_classification_id TYPE BIGINT,
    ALTER COLUMN statistical_classification_id SET NOT NULL;

-- drop old constraint before adding new

ALTER TABLE level
    DROP CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4;

ALTER TABLE level
    ADD CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4 FOREIGN KEY (
    statistical_classification_id) REFERENCES statistical_classification (id);

