-- renames a foreign key column

ALTER TABLE correspondence_map
  DROP FOREIGN KEY fk_74gwtjl4rjw3gw4x9idj4ux98;

ALTER TABLE correspondence_map CHANGE correspondence_table correspondence_table_id BIGINT not null;

ALTER TABLE correspondence_map
  ADD CONSTRAINT fk_74gwtjl4rjw3gw4x9idj4ux98 FOREIGN KEY (correspondence_table_id)
  REFERENCES correspondence_table (id);

