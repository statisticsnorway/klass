-- Create backup table
CREATE TABLE IF NOT EXISTS klass.user_section_backup AS
SELECT id, section
FROM klass."user";

-- Safe update
UPDATE klass."user"
SET section = CASE
    -- Sections containing ' - '
                  WHEN section LIKE '% - %' THEN TRIM(split_part(section, ' - ', 1))

    -- Sections that are only digits (may have whitespace)
                  WHEN section ~ '^\d+$' THEN TRIM(split_part(section, ' - ', 1))

    -- Special case: sections like 'O 330...'
                  WHEN section ILIKE '%O 330%' THEN TRIM(split_part(section, ' ', 2))

    -- No numbers in value → replace with empty string
                  WHEN section !~ '[0-9]' THEN ''

    -- Keep original (trimmed)
                  ELSE TRIM(section)
    END
WHERE section LIKE '% - %'
   OR section ~ '^\d+$'
   OR section ILIKE '%O 330%'
   OR section !~ '[0-9]';


