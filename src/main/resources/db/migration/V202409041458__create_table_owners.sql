CREATE TABLE owner (
                       id SERIAL primary key,
                       name VARCHAR(30) NOT NULL DEFAULT 'Unknown',
                       company_id BIGINT NOT NULL DEFAULT 0,
                       employee_id VARCHAR NOT NULL DEFAULT 'Unknown'
);

CREATE INDEX idx_owner_table_company_employee ON owner (company_id, employee_id);

/*
INSERT INTO owner (name, company_id, employee_id)
VALUES ('Josh', '9', 123123123);

INSERT INTO owner (name, company_id, employee_id)
VALUES ('Ariel', '9', 999123123);
*/
