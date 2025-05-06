ALTER TABLE changelog
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified,
    ALTER COLUMN change_occured TYPE timestamp(0) USING change_occured;

ALTER TABLE classification_access_counter
    ALTER COLUMN time_stamp TYPE timestamp(0) USING time_stamp;

ALTER TABLE classification_family
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE classification_series
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE correspondence_map
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE correspondence_table
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE level
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE search_words
    ALTER COLUMN time_stamp TYPE timestamp(0) USING time_stamp;

ALTER TABLE statistical_classification
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE statistical_unit
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE subscriber
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;

ALTER TABLE subscription
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified,
    ALTER COLUMN expiry_date TYPE timestamp(0) USING expiry_date;

ALTER TABLE classification_item
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;


ALTER TABLE "user"
    ALTER COLUMN last_modified TYPE timestamp(0) USING last_modified;
