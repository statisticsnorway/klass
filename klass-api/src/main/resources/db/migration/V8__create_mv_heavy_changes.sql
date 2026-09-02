-- Materialized view for large classifications and long timespan change queries.
-- Flyway-safe approach: create schema objects first, then refresh explicitly.
DROP MATERIALIZED VIEW IF EXISTS klass.mv_heavy_changes;

CREATE MATERIALIZED VIEW klass.mv_heavy_changes AS
WITH versions AS (
    SELECT
        sc.id AS version_id,
        sc.classification_id,
        sc.deleted,
        CASE WHEN sc.valid_from ~ '^\d{4}-\d{2}-\d{2}$' THEN sc.valid_from::date END AS valid_from_date,
        CASE WHEN sc.valid_to   ~ '^\d{4}-\d{2}-\d{2}$' THEN sc.valid_to::date   END AS valid_to_date
    FROM klass.statistical_classification sc
    WHERE sc.dtype = 'version'
      AND sc.classification_id IN (131, 6)
),
     non_draft_versions AS (
         SELECT *
         FROM versions
         WHERE valid_from_date IS NOT NULL
           AND valid_to_date IS NOT NULL
           AND deleted = false
     ),
     ordered_versions AS (
         SELECT
             classification_id,
             version_id AS v1_id,
             LEAD(version_id) OVER (PARTITION BY classification_id ORDER BY valid_from_date, version_id) AS v2_id
         FROM non_draft_versions
     ),
     pairs AS (
         SELECT classification_id, v1_id, v2_id
         FROM ordered_versions
         WHERE v2_id IS NOT NULL
     ),
     change_tables AS (
         SELECT
             p.classification_id,
             ct.id AS correspondence_table_id,
             ct.source_id,
             ct.target_id
         FROM pairs p
                  JOIN klass.correspondence_table ct
                       ON (ct.source_id = p.v1_id AND ct.target_id = p.v2_id)
                           OR (ct.source_id = p.v2_id AND ct.target_id = p.v1_id)
         WHERE ct.deleted = false
           AND ct.draft = false
     )
SELECT
    c.classification_id,
    c.correspondence_table_id,
    cm.id AS correspondence_map_id,
    GREATEST(src.valid_from_date, tgt.valid_from_date) AS change_occurred,
    CASE WHEN tgt.valid_from_date < src.valid_from_date THEN ci_t.code ELSE ci_s.code END AS old_code,
    CASE WHEN tgt.valid_from_date < src.valid_from_date THEN ci_s.code ELSE ci_t.code END AS new_code
FROM change_tables c
         JOIN non_draft_versions src ON src.version_id = c.source_id
         JOIN non_draft_versions tgt ON tgt.version_id = c.target_id
         JOIN klass.correspondence_map cm ON cm.correspondence_table_id = c.correspondence_table_id
         LEFT JOIN klass.classification_item ci_s ON ci_s.id = cm.source_id
         LEFT JOIN klass.classification_item ci_t ON ci_t.id = cm.target_id
WHERE GREATEST(src.valid_from_date, tgt.valid_from_date) >= DATE '1900-01-01'
WITH NO DATA;

-- Required if you later use REFRESH MATERIALIZED VIEW CONCURRENTLY.
CREATE UNIQUE INDEX ux_mv_heavy_changes_map
    ON klass.mv_heavy_changes (correspondence_map_id);

CREATE INDEX ix_mv_heavy_changes_class_date
    ON klass.mv_heavy_changes (classification_id, change_occurred);