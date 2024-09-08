-- V1__create_pets_table.sql

CREATE TABLE pets (
                      id SERIAL primary key,
                      name VARCHAR(30) NOT NULL,
                      type VARCHAR(15) NOT NULL,
                      company_id INT NOT NULL,
                      date_of_arrival DATE NOT NULL
);

CREATE INDEX idx_company_id ON pets (company_id);

INSERT INTO public.pets (name, type, company_id, date_of_arrival)
VALUES ('Witi', 'Dog', 1, '2020-01-28');
INSERT INTO public.pets (name, type, company_id, date_of_arrival)
VALUES ('Piti', 'Cat', 1, '2016-01-28');
INSERT INTO public.pets (name, type, company_id, date_of_arrival)
VALUES ('Bobi', 'Dog', 2, '2023-02-24');

SELECT * FROM pets
WHERE type = 'Dog';

-- Delete pet by ID (e.g., id = 1)
DELETE FROM pets
WHERE id = 1;

SELECT *
FROM pets
WHERE date_of_arrival < CURRENT_DATE - INTERVAL '1 year';

