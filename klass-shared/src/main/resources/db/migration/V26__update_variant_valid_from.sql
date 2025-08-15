--# create table
CREATE TABLE temp_variant_update
(
    variant_id INT,
    version_id INT,
    valid_from VARCHAR(16),
    valid_to VARCHAR(16)
);

--# add IDs
insert into temp_variant_update (variant_id, version_id, valid_from,valid_to )
  select b.id, b.classification_version_id, b.valid_from, b.valid_to
  from statistical_classification as b
  where b.dtype = 'variant';

--# add timestamps
update temp_variant_update as tmp set  valid_from = (select valid_from from  statistical_classification  where id = tmp.version_id);
update temp_variant_update as tmp set  valid_to = (select valid_to from  statistical_classification  where id = tmp.version_id);

--# set from
update statistical_classification a set a.valid_from =
(SELECT b.valid_from
                    FROM temp_variant_update AS b
                    WHERE a.id = b.variant_id
)
where exists(SELECT * from temp_variant_update where version_id = a.CLASSIFICATION_VERSION_ID);
--# set to
update statistical_classification a set a.valid_to =
(SELECT b.valid_to
                    FROM temp_variant_update AS b
                    WHERE a.id = b.variant_id
)
where exists(SELECT * from temp_variant_update where version_id = a.CLASSIFICATION_VERSION_ID);
--# clean up
drop TABLE  temp_variant_update;

