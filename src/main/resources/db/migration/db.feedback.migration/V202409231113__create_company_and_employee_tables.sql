CREATE TABLE company
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);


CREATE TABLE employee (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    employee_id INT NOT NULL unique,
    department VARCHAR(30) NOT NULL
);

