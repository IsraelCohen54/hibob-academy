CREATE TABLE owners (
                       id SERIAL primary key,
                       name VARCHAR(30) NOT NULL,
                       company_id BIGINT NOT NULL,
                       employee_id VARCHAR NOT NULL
);

CREATE unique INDEX idx_owner_company_employee ON owners (company_id, employee_id);

/*
INSERT INTO owners (name, company_id, employee_id)
VALUES ('Josh', '9', 123123123);

INSERT INTO owners (name, company_id, employee_id)
VALUES ('Ariel', '9', 999123123);
*/
