
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


CREATE TABLE job (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(5000),
    created_at TIMESTAMP,
    created_by_id BIGINT,
    CONSTRAINT fk_job_created_by FOREIGN KEY (created_by_id) REFERENCES users(id)
);


CREATE TABLE cv (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    candidate_first_name VARCHAR(255),
    candidate_last_name VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(50),
    cv_content TEXT,
    upload_time TIMESTAMP,
    uploaded_by_id BIGINT,
    job_id BIGINT,
    CONSTRAINT fk_cv_uploaded_by FOREIGN KEY (uploaded_by_id) REFERENCES users(id),
    CONSTRAINT fk_cv_job FOREIGN KEY (job_id) REFERENCES job(id)
);
