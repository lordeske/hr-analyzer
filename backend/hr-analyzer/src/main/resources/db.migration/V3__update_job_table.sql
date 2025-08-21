ALTER TABLE job


    DROP COLUMN source_url,
    DROP COLUMN external_id,
    DROP COLUMN description_snapshot;

ALTER TABLE job
    ADD COLUMN description VARCHAR(5000);

