-- Create initial version of database

CREATE TABLE classification_family
  (
     id            BIGINT NOT NULL auto_increment,
     deleted       BIT NOT NULL,
     last_modified DATETIME NOT NULL,
     uuid          VARCHAR(255) NOT NULL,
     version       INTEGER NOT NULL,
     icon_name     VARCHAR(255) NOT NULL,
     name          VARCHAR(255) NOT NULL,
     PRIMARY KEY (id)
  );

CREATE TABLE classification_item
  (
     dtype         VARCHAR(31) NOT NULL,
     id            BIGINT NOT NULL auto_increment,
     deleted       BIT NOT NULL,
     last_modified DATETIME NOT NULL,
     uuid          VARCHAR(255) NOT NULL,
     version       INTEGER NOT NULL,
     code          VARCHAR(255) NOT NULL,
     notes         VARCHAR(4000),
     official_name VARCHAR(1024) NOT NULL,
     short_name    VARCHAR(1024),
     level_id      BIGINT NOT NULL,
     parent_id     BIGINT,
     reference_id  BIGINT,
     PRIMARY KEY (id)
  );

CREATE TABLE classification_series
  (
     id                       BIGINT NOT NULL auto_increment,
     deleted                  BIT NOT NULL,
     last_modified            DATETIME NOT NULL,
     uuid                     VARCHAR(255) NOT NULL,
     version                  INTEGER NOT NULL,
     classification_type      VARCHAR(255) NOT NULL,
     copyrighted              BIT NOT NULL,
     description              VARCHAR(10000) NOT NULL,
     include_notes            BIT NOT NULL,
     include_short_name       BIT NOT NULL,
     internal                 BIT NOT NULL,
     name_en                  VARCHAR(255),
     name_nn                  VARCHAR(255),
     name_no                  VARCHAR(255),
     primary_language         INTEGER NOT NULL,
     classification_family_id BIGINT NOT NULL,
     contact_person_id        BIGINT NOT NULL,
     PRIMARY KEY (id)
  );

CREATE TABLE classification_series_statistical_units
  (
     classification_series_id BIGINT NOT NULL,
     statistical_units_id     BIGINT NOT NULL
  );

CREATE TABLE correspondence_map
  (
     id                   BIGINT NOT NULL auto_increment,
     deleted              BIT NOT NULL,
     last_modified        DATETIME NOT NULL,
     uuid                 VARCHAR(255) NOT NULL,
     version              INTEGER NOT NULL,
     source_id            BIGINT,
     target_id            BIGINT,
     correspondence_table BIGINT,
     PRIMARY KEY (id)
  );

CREATE TABLE correspondence_table
  (
     id                         BIGINT NOT NULL auto_increment,
     deleted                    BIT NOT NULL,
     last_modified              DATETIME NOT NULL,
     uuid                       VARCHAR(255) NOT NULL,
     version                    INTEGER NOT NULL,
     description                VARCHAR(255) NOT NULL,
     published_en               BIT NOT NULL,
     published_nn               BIT NOT NULL,
     published_no               BIT NOT NULL,
     source_id                  BIGINT NOT NULL,
     source_level_id            BIGINT,
     target_id                  BIGINT NOT NULL,
     target_level_id            BIGINT,
     statistical_classification BIGINT,
     PRIMARY KEY (id)
  );

CREATE TABLE level
  (
     id                         BIGINT NOT NULL auto_increment,
     deleted                    BIT NOT NULL,
     last_modified              DATETIME NOT NULL,
     uuid                       VARCHAR(255) NOT NULL,
     version                    INTEGER NOT NULL,
     level_number               INTEGER NOT NULL,
     name                       VARCHAR(255) NOT NULL,
     statistical_classification BIGINT,
     PRIMARY KEY (id)
  );

CREATE TABLE statistical_classification
  (
     dtype                     VARCHAR(31) NOT NULL,
     id                        BIGINT NOT NULL auto_increment,
     deleted                   BIT NOT NULL,
     last_modified             DATETIME NOT NULL,
     uuid                      VARCHAR(255) NOT NULL,
     version                   INTEGER NOT NULL,
     introduction              VARCHAR(255) NOT NULL,
     published_en              BIT NOT NULL,
     published_nn              BIT NOT NULL,
     published_no              BIT NOT NULL,
     name                      VARCHAR(255),
     derived_from              VARCHAR(255),
     legal_base                VARCHAR(255),
     publications              VARCHAR(255),
     valid_from                VARCHAR(255),
     valid_to                  VARCHAR(255),
     classification_version_id BIGINT,
     contact_person_id         BIGINT,
     classification_id         BIGINT,
     predecessor_id            BIGINT,
     successor_id              BIGINT,
     PRIMARY KEY (id)
  );

CREATE TABLE statistical_unit
  (
     id            BIGINT NOT NULL auto_increment,
     deleted       BIT NOT NULL,
     last_modified DATETIME NOT NULL,
     uuid          VARCHAR(255) NOT NULL,
     version       INTEGER NOT NULL,
     name          VARCHAR(255) NOT NULL,
     PRIMARY KEY (id)
  );

CREATE TABLE subscriber
  (
     id            BIGINT NOT NULL auto_increment,
     deleted       BIT NOT NULL,
     last_modified DATETIME NOT NULL,
     uuid          VARCHAR(255) NOT NULL,
     version       INTEGER NOT NULL,
     email         VARCHAR(255) NOT NULL,
     PRIMARY KEY (id)
  );

CREATE TABLE subscription
  (
     id                BIGINT NOT NULL auto_increment,
     deleted           BIT NOT NULL,
     last_modified     DATETIME NOT NULL,
     uuid              VARCHAR(255) NOT NULL,
     version           INTEGER NOT NULL,
     expiry_date       DATETIME NOT NULL,
     token             VARCHAR(255) NOT NULL,
     verification      INTEGER NOT NULL,
     classification_id BIGINT,
     subscriber        BIGINT,
     PRIMARY KEY (id)
  );

CREATE TABLE user
  (
     id            BIGINT NOT NULL auto_increment,
     deleted       BIT NOT NULL,
     last_modified DATETIME NOT NULL,
     uuid          VARCHAR(255) NOT NULL,
     version       INTEGER NOT NULL,
     fullname      VARCHAR(255) NOT NULL,
     role          INTEGER NOT NULL,
     section       VARCHAR(255) NOT NULL,
     username      VARCHAR(255) NOT NULL,
     PRIMARY KEY (id)
  );

CREATE TABLE user_favorites
  (
     user_id      BIGINT NOT NULL,
     favorites_id BIGINT NOT NULL
  );

ALTER TABLE classification_family
  ADD CONSTRAINT cf_name_idx UNIQUE (name);

ALTER TABLE classification_series
  ADD CONSTRAINT cs_name_no_idx UNIQUE (name_no);

ALTER TABLE classification_series
  ADD CONSTRAINT cs_name_nn_idx UNIQUE (name_nn);

ALTER TABLE classification_series
  ADD CONSTRAINT cs_name_en_idx UNIQUE (name_en);

CREATE INDEX ct_source_idx ON correspondence_table (source_id);

CREATE INDEX ct_target_idx ON correspondence_table (target_id);

ALTER TABLE subscriber
  ADD CONSTRAINT subscriber_email_idx UNIQUE (email);

ALTER TABLE user
  ADD CONSTRAINT user_username_idx UNIQUE (username);

CREATE INDEX user_fullname_idx ON user (fullname);

ALTER TABLE user_favorites
  ADD CONSTRAINT uk_dk8ngwbk9dgeuegrx1nuktdv1 UNIQUE (favorites_id);

ALTER TABLE classification_item
  ADD CONSTRAINT fk_g9g8ko4hiflhcdvt7431oofli FOREIGN KEY (level_id) REFERENCES
  level (id);

ALTER TABLE classification_item
  ADD CONSTRAINT fk_r370q2nlapshi9qrvswx98vrm FOREIGN KEY (parent_id) REFERENCES
  classification_item (id);

ALTER TABLE classification_item
  ADD CONSTRAINT fk_ge0nq2ndexf8l2cmvppl66qh3 FOREIGN KEY (reference_id)
  REFERENCES classification_item (id);

ALTER TABLE classification_series
  ADD CONSTRAINT fk_666t6jamu95kshjhawy2co5ej FOREIGN KEY (
  classification_family_id) REFERENCES classification_family (id);

ALTER TABLE classification_series
  ADD CONSTRAINT fk_jefvdo01kn4kq98m64ajli6y5 FOREIGN KEY (contact_person_id)
  REFERENCES user (id);

ALTER TABLE classification_series_statistical_units
  ADD CONSTRAINT fk_rfx4t7rjg3hykfr39xi8dvrdu FOREIGN KEY (statistical_units_id)
  REFERENCES statistical_unit (id);

ALTER TABLE classification_series_statistical_units
  ADD CONSTRAINT fk_6nl7ywwll2hvo9ucndhg1b1xy FOREIGN KEY (
  classification_series_id) REFERENCES classification_series (id);

ALTER TABLE correspondence_map
  ADD CONSTRAINT fk_fiq7wlm30w4ixosjqpob2m077 FOREIGN KEY (source_id) REFERENCES
  classification_item (id);

ALTER TABLE correspondence_map
  ADD CONSTRAINT fk_polg03jsn36cq2pujlee5k13y FOREIGN KEY (target_id) REFERENCES
  classification_item (id);

ALTER TABLE correspondence_map
  ADD CONSTRAINT fk_74gwtjl4rjw3gw4x9idj4ux98 FOREIGN KEY (correspondence_table)
  REFERENCES correspondence_table (id);

ALTER TABLE correspondence_table
  ADD CONSTRAINT fk_aoqrb4hktku6syonwjfwd3055 FOREIGN KEY (source_id) REFERENCES
  statistical_classification (id);

ALTER TABLE correspondence_table
  ADD CONSTRAINT fk_8bn11tm1gsou84v8kmoug6qyt FOREIGN KEY (source_level_id)
  REFERENCES level (id);

ALTER TABLE correspondence_table
  ADD CONSTRAINT fk_76of0l6um26nystb8dqwbj4x1 FOREIGN KEY (target_id) REFERENCES
  statistical_classification (id);

ALTER TABLE correspondence_table
  ADD CONSTRAINT fk_cqdqwek3akelq2qqnhuydrkqr FOREIGN KEY (target_level_id)
  REFERENCES level (id);

ALTER TABLE correspondence_table
  ADD CONSTRAINT fk_4tx0j45sopr6yj8wcedohj9w3 FOREIGN KEY (
  statistical_classification) REFERENCES statistical_classification (id);

ALTER TABLE level
  ADD CONSTRAINT fk_sxgu9ljubog3jnujn8fvohvn4 FOREIGN KEY (
  statistical_classification) REFERENCES statistical_classification (id);

ALTER TABLE statistical_classification
  ADD CONSTRAINT fk_e2pju4h6p0pdlbgievunfqrtg FOREIGN KEY (
  classification_version_id) REFERENCES statistical_classification (id);

ALTER TABLE statistical_classification
  ADD CONSTRAINT fk_12hsnu91tsf1c8b4697tv1bts FOREIGN KEY (contact_person_id)
  REFERENCES user (id);

ALTER TABLE statistical_classification
  ADD CONSTRAINT fk_ie31cb245vkf1vhswws053s29 FOREIGN KEY (classification_id)
  REFERENCES classification_series (id);

ALTER TABLE statistical_classification
  ADD CONSTRAINT fk_ije1678rnf2dso2pm83xchu9d FOREIGN KEY (predecessor_id)
  REFERENCES statistical_classification (id);

ALTER TABLE statistical_classification
  ADD CONSTRAINT fk_tdivea13yro7vtbon5l0elqtg FOREIGN KEY (successor_id)
  REFERENCES statistical_classification (id);

ALTER TABLE subscription
  ADD CONSTRAINT fk_in6dab29f2fdp035fja02e2k3 FOREIGN KEY (classification_id)
  REFERENCES classification_series (id);

ALTER TABLE subscription
  ADD CONSTRAINT fk_d9uwbiv0kt50iw4fcfydc7te7 FOREIGN KEY (subscriber)
  REFERENCES subscriber (id);

ALTER TABLE user_favorites
  ADD CONSTRAINT fk_dk8ngwbk9dgeuegrx1nuktdv1 FOREIGN KEY (favorites_id)
  REFERENCES classification_series (id);

ALTER TABLE user_favorites
  ADD CONSTRAINT fk_dx271ymxhckcafibaqijpda8h FOREIGN KEY (user_id) REFERENCES
  user (id);