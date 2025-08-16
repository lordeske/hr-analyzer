
ALTER TABLE job
    DROP COLUMN description;

ALTER TABLE job
    ADD COLUMN company VARCHAR(255),
    ADD COLUMN location VARCHAR(255),
    ADD COLUMN source_url VARCHAR(2000),
    ADD COLUMN external_id VARCHAR(100) UNIQUE,
    ADD COLUMN description_snapshot TEXT NOT NULL DEFAULT '';
