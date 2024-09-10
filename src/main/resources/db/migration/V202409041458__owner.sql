CREATE TABLE owner (
                       id SERIAL primary key,
                       name VARCHAR(30) NOT NULL,
                       company_id INT NOT NULL,
                       employee_id INT NOT NULL
);

CREATE INDEX idx_company_employee ON owner (company_id, employee_id);

/*
INSERT INTO owner (name, company_id, employee_id)
VALUES ('Josh', '9', 123123123);

INSERT INTO owner (name, company_id, employee_id)
VALUES ('Ariel', '9', 999123123);
*/