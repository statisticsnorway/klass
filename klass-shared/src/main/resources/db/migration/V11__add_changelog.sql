CREATE TABLE changelog
  (
     id             BIGINT NOT NULL auto_increment,
     last_modified  DATETIME NOT NULL,
     uuid           VARCHAR(255) NOT NULL,
     version        INTEGER NOT NULL,
     change_occured DATETIME NOT NULL,
     changed_by     VARCHAR(255) NOT NULL,
     description    VARCHAR(4096) NOT NULL,
     PRIMARY KEY (id)
  );

CREATE TABLE statisticalclassification_changelog
  (
     statisticalclassification_id BIGINT NOT NULL,
     changelog_id                 BIGINT NOT NULL
  );

ALTER TABLE statisticalclassification_changelog
  ADD CONSTRAINT uk_k1e2lk17i4v7t76hequ2cegri UNIQUE (changelog_id);

ALTER TABLE statisticalclassification_changelog
  ADD CONSTRAINT fk_k1e2lk17i4v7t76hequ2cegri FOREIGN KEY (changelog_id)
  REFERENCES changelog (id);

ALTER TABLE statisticalclassification_changelog
  ADD CONSTRAINT fk_fhfff2m0ltcoqt9hxbnsw3u6j FOREIGN KEY (
  statisticalclassification_id) REFERENCES statistical_classification (id);


CREATE TABLE correspondencetable_changelog
  (
     correspondencetable_id BIGINT NOT NULL,
     changelog_id           BIGINT NOT NULL
  );

ALTER TABLE correspondencetable_changelog
  ADD CONSTRAINT uk_8gl7hjwhs4c6iiih7myp4dtud UNIQUE (changelog_id);

ALTER TABLE correspondencetable_changelog
  ADD CONSTRAINT fk_8gl7hjwhs4c6iiih7myp4dtud FOREIGN KEY (changelog_id)
  REFERENCES changelog (id);

ALTER TABLE correspondencetable_changelog
  ADD CONSTRAINT fk_9obbpbvq6quql391al79il4l0 FOREIGN KEY (
  correspondencetable_id) REFERENCES correspondence_table (id);  