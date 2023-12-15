ALTER TABLE user_favorites
  RENAME TO user_favorites_old;

CREATE TABLE user_favorites
(
  id           BIGINT NOT NULL AUTO_INCREMENT,
  user_id      BIGINT NOT NULL,
  favorites_id BIGINT NOT NULL,
  PRIMARY KEY (id)
);
ALTER TABLE user_favorites ADD CONSTRAINT fk_favorite_id FOREIGN KEY (favorites_id) REFERENCES classification_series (id);
ALTER TABLE user_favorites ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user" (id);
INSERT INTO user_favorites (user_id, favorites_id) SELECT
                                                     user_id,
                                                     favorites_id
                                                   FROM user_favorites_old;

DROP TABLE user_favorites_old;

