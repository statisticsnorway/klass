-- due to row limits of 65535  we need to reduce size for some fields to  be able to increase name field
-- reduce
ALTER TABLE statistical_classification MODIFY valid_from VARCHAR(16);
ALTER TABLE statistical_classification MODIFY valid_to VARCHAR(16);
ALTER TABLE statistical_classification MODIFY uuid VARCHAR(64) NOT NULL;

-- increase
ALTER TABLE statistical_classification MODIFY name VARCHAR(1000);
