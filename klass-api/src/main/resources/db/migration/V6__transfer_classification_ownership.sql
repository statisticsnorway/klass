-- =========================================================
-- 1. Validate duplicate users and ownership mappings
-- =========================================================
DO $$
    DECLARE
        invalid_groups text;
    BEGIN
        SELECT string_agg(
                       format(
                               '%s: %s',
                               fullname,
                               usernames
                       ),
                       E'\n'
               )
        INTO invalid_groups
        FROM (
                 SELECT
                     fullname,
                     array_agg(username ORDER BY username) AS usernames
                 FROM klass."user"
                 GROUP BY fullname
                 HAVING COUNT(*) = 2
                    AND NOT (
                             COUNT(*) FILTER (WHERE username LIKE '%@%') = 1
                         AND COUNT(*) FILTER (WHERE username NOT LIKE '%@%') = 1
                     )
             ) invalid;

        IF invalid_groups IS NOT NULL THEN
            RAISE EXCEPTION
                E'Found duplicate groups with 2 users which does not adhere to pattern:\n%',
                invalid_groups;
        END IF;
    END $$;

-- =========================================================
-- 2. Transfer classification ownership
-- =========================================================

WITH user_groups AS (
    SELECT
        fullname
    FROM klass."user"
    GROUP BY fullname
    HAVING COUNT(*) = 2
       AND COUNT(*) FILTER (WHERE username LIKE '%@%') = 1
       AND COUNT(*) FILTER (WHERE username NOT LIKE '%@%') = 1
),
     user_mapping AS (
         SELECT
             duplicate.fullname,
             duplicate.id AS duplicate_user_id,
             correct.id AS correct_user_id
         FROM user_groups g
                  JOIN klass."user" duplicate
                       ON duplicate.fullname = g.fullname
                           AND duplicate.username LIKE '%@%'
                  JOIN klass."user" correct
                       ON correct.fullname = g.fullname
                           AND correct.username NOT LIKE '%@%'
     )
UPDATE klass.classification_series cs
SET contact_person_id = m.correct_user_id
FROM user_mapping m
WHERE cs.contact_person_id = m.duplicate_user_id;