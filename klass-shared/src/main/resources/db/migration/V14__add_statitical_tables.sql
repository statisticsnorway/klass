CREATE TABLE classification_access_counter 
    (
        id                              BIGINT NOT NULL auto_increment,
        classification_series_id        BIGINT,
        correspondence_table_id         BIGINT,
        classification_version_id       BIGINT,
        time_stamp                      DATETIME NOT NULL,
        PRIMARY KEY (id)
    );
    
CREATE TABLE search_words
    (
        id                              BIGINT NOT NULL auto_increment,        
        time_stamp                      DATETIME NOT NULL,
        hit                             BIT NOT NULL,
        search_string                   VARCHAR(255) NOT NULL,
        PRIMARY KEY (id)
    );
    