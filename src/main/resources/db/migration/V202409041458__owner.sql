-- V1__create_owner_table.sql

CREATE TABLE owner (
                       name VARCHAR(30) NOT NULL,
                       company_id INT NOT NULL,
                       employee_id INT NOT NULL
);

CREATE INDEX idx_company_employee ON owner (company_id, employee_id);
