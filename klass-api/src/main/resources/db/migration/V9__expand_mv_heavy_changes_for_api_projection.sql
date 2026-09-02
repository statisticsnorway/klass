-- Expand mv_heavy_changes with precomputed per-language name/short-name columns.
-- This shifts expensive XML parsing out of the request path.
DROP MATERIALIZED VIEW IF EXISTS klass.mv_heavy_changes;

CREATE MATERIALIZED VIEW klass.mv_heavy_changes AS
WITH versions AS (
    SELECT
        sc.id AS version_id,
        sc.classification_id,
        sc.deleted,
        CASE WHEN sc.valid_from ~ '^\d{4}-\d{2}-\d{2}$' THEN sc.valid_from::date END AS valid_from_date,
        CASE WHEN sc.valid_to ~ '^\d{4}-\d{2}-\d{2}$' THEN sc.valid_to::date END AS valid_to_date
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
        LEAD(version_id) OVER (PARTITION BY classification_id ORDER BY valid_from_date, version_id)
                AS v2_id
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
        ct.target_id,
        ct.published_no,
        ct.published_nn,
        ct.published_en
    FROM pairs p
    JOIN klass.correspondence_table ct
      ON (ct.source_id = p.v1_id AND ct.target_id = p.v2_id)
      OR (ct.source_id = p.v2_id AND ct.target_id = p.v1_id)
    WHERE ct.deleted = false
      AND ct.draft = false
),
base_data AS (
    SELECT
        c.classification_id,
        c.correspondence_table_id,
        cm.id AS correspondence_map_id,
        GREATEST(src.valid_from_date, tgt.valid_from_date) AS change_occurred,
        c.published_no,
        c.published_nn,
        c.published_en,
        (tgt.valid_from_date < src.valid_from_date) AS target_oldest,
        ci_s.code AS source_code,
        ci_t.code AS target_code,
        ci_s.official_name AS source_official_name,
        ci_t.official_name AS target_official_name,
        ci_s.short_name AS source_short_name,
        ci_t.short_name AS target_short_name
    FROM change_tables c
    JOIN non_draft_versions src ON src.version_id = c.source_id
    JOIN non_draft_versions tgt ON tgt.version_id = c.target_id
    JOIN klass.correspondence_map cm ON cm.correspondence_table_id = c.correspondence_table_id
    LEFT JOIN klass.classification_item ci_s ON ci_s.id = cm.source_id
    LEFT JOIN klass.classification_item ci_t ON ci_t.id = cm.target_id
    WHERE GREATEST(src.valid_from_date, tgt.valid_from_date) >= DATE '1900-01-01'
)
SELECT
    classification_id,
    correspondence_table_id,
    correspondence_map_id,
    change_occurred,
    published_no,
    published_nn,
    published_en,
    CASE WHEN target_oldest THEN target_code ELSE source_code END AS old_code,
    CASE WHEN target_oldest THEN source_code ELSE target_code END AS new_code,
    CASE WHEN target_oldest
            THEN (regexp_match(target_official_name, '<no>(.*?)</no>'))[1]
            ELSE (regexp_match(source_official_name, '<no>(.*?)</no>'))[1]
    END AS old_name_no,
    CASE WHEN target_oldest
            THEN (regexp_match(source_official_name, '<no>(.*?)</no>'))[1]
            ELSE (regexp_match(target_official_name, '<no>(.*?)</no>'))[1]
    END AS new_name_no,
    CASE WHEN target_oldest
            THEN (regexp_match(target_official_name, '<nn>(.*?)</nn>'))[1]
            ELSE (regexp_match(source_official_name, '<nn>(.*?)</nn>'))[1]
    END AS old_name_nn,
    CASE WHEN target_oldest
            THEN (regexp_match(source_official_name, '<nn>(.*?)</nn>'))[1]
            ELSE (regexp_match(target_official_name, '<nn>(.*?)</nn>'))[1]
    END AS new_name_nn,
    CASE WHEN target_oldest
            THEN (regexp_match(target_official_name, '<en>(.*?)</en>'))[1]
            ELSE (regexp_match(source_official_name, '<en>(.*?)</en>'))[1]
    END AS old_name_en,
    CASE WHEN target_oldest
            THEN (regexp_match(source_official_name, '<en>(.*?)</en>'))[1]
            ELSE (regexp_match(target_official_name, '<en>(.*?)</en>'))[1]
    END AS new_name_en,
    CASE WHEN target_oldest
            THEN (regexp_match(target_short_name, '<no>(.*?)</no>'))[1]
            ELSE (regexp_match(source_short_name, '<no>(.*?)</no>'))[1]
    END AS old_short_name_no,
    CASE WHEN target_oldest
            THEN (regexp_match(source_short_name, '<no>(.*?)</no>'))[1]
            ELSE (regexp_match(target_short_name, '<no>(.*?)</no>'))[1]
    END AS new_short_name_no,
    CASE WHEN target_oldest
            THEN (regexp_match(target_short_name, '<nn>(.*?)</nn>'))[1]
            ELSE (regexp_match(source_short_name, '<nn>(.*?)</nn>'))[1]
    END AS old_short_name_nn,
    CASE WHEN target_oldest
            THEN (regexp_match(source_short_name, '<nn>(.*?)</nn>'))[1]
            ELSE (regexp_match(target_short_name, '<nn>(.*?)</nn>'))[1]
    END AS new_short_name_nn,
    CASE WHEN target_oldest
            THEN (regexp_match(target_short_name, '<en>(.*?)</en>'))[1]
            ELSE (regexp_match(source_short_name, '<en>(.*?)</en>'))[1]
    END AS old_short_name_en,
    CASE WHEN target_oldest
            THEN (regexp_match(source_short_name, '<en>(.*?)</en>'))[1]
            ELSE (regexp_match(target_short_name, '<en>(.*?)</en>'))[1]
    END AS new_short_name_en
FROM base_data
WITH NO DATA;

-- Required for REFRESH MATERIALIZED VIEW CONCURRENTLY.
CREATE UNIQUE INDEX ux_mv_heavy_changes_map
    ON klass.mv_heavy_changes (correspondence_map_id);

CREATE INDEX ix_mv_heavy_changes_class_date
    ON klass.mv_heavy_changes (classification_id, change_occurred);

