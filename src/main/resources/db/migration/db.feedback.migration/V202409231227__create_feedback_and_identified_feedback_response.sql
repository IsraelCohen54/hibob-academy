CREATE TABLE feedback (
    feedback_id BIGSERIAL primary key,
    company_id BIGINT NOT NULL,
    department VARCHAR(30),
    feedback VARCHAR(200) NOT NULL,
    date_given DATE DEFAULT CURRENT_DATE,
    status BOOLEAN DEFAULT FALSE,
    employee_id INT
);

CREATE unique INDEX idx_feedback_on_company_id_feedback_employee_id ON feedback (company_id, feedback, employee_id);

CREATE TABLE identified_feedback_response
(
    id BIGSERIAL PRIMARY KEY,
    feedback_id BIGINT NOT NULL unique,
    company_id BIGINT NOT NULL,
    response VARCHAR(300) NOT NULL
);