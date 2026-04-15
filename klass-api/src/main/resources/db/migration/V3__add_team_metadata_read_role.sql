-- Role for shared permissions
CREATE ROLE team_metadata_read;

-- Allow access to schema
GRANT USAGE ON SCHEMA klass TO team_metadata_read;

-- Existing tables
GRANT SELECT ON ALL TABLES IN SCHEMA klass TO team_metadata_read;

-- Future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA klass
GRANT SELECT ON TABLES TO team_metadata_read;



