CREATE TABLE feedback (
    id BIGSERIAL primary key,
    company_id BIGINT NOT NULL,
    department VARCHAR(30),
    comment VARCHAR(200) NOT NULL,
    creation_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'NOT_SOLVED',
    employee_id INT
);

CREATE unique INDEX idx_feedback_on_company_id_feedback_employee_id ON feedback (company_id, feedback, employee_id);

CREATE TABLE identified_feedback_response
(
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    feedback_id BIGINT NOT NULL unique,
    response VARCHAR(300) NOT NULL
);