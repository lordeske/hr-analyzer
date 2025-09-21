
ALTER TABLE cv
ADD COLUMN candidate_id BIGINT;


ALTER TABLE cv
ADD CONSTRAINT fk_cv_candidate
FOREIGN KEY (candidate_id) REFERENCES "users"(id);


ALTER TABLE cv
    DROP COLUMN IF EXISTS candidate_first_name,
    DROP COLUMN IF EXISTS candidate_last_name,
    DROP COLUMN IF EXISTS email,
    DROP COLUMN IF EXISTS phone_number,
    DROP COLUMN IF EXISTS uploaded_by_id,
    DROP COLUMN IF EXISTS uploadedBy_id;
