CREATE TABLE cv (
    id SERIAL PRIMARY KEY,
    candidate_first_name VARCHAR(255),
    candidate_last_name VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(255),
    job_title VARCHAR(255),
    cv_content TEXT,
    match_score DOUBLE PRECISION,
    upload_time TIMESTAMP,
    uploaded_by_id BIGINT,

    CONSTRAINT fk_uploaded_by
        FOREIGN KEY (uploaded_by_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);
