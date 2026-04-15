-- Role for shared permissions
CREATE ROLE klass_read;

-- Allow access to schema
GRANT USAGE ON SCHEMA klass TO klass_read;

-- Existing tables
GRANT SELECT ON ALL TABLES IN SCHEMA klass TO klass_read;

-- Future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA klass
GRANT SELECT ON TABLES TO klass_read;



