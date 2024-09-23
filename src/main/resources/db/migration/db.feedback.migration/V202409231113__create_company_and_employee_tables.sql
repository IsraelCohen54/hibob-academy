CREATE TABLE company
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);


CREATE TABLE employee (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) CHECK (role IN ('admin', 'manager', 'employee')) NOT NULL,
    company_id BIGINT REFERENCES company(id),
    employee_id INT NOT NULL unique,
    department VARCHAR(30) NOT NULL
);

