CREATE TABLE vaccine_to_pet (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(30) NOT NULL,
                      vaccination_date DATE DEFAULT CURRENT_DATE,
                      pet_id BIGINT NOT NULL
);

CREATE unique INDEX idx_vaccine_to_pet_name_date_pet_id ON vaccine_to_pet (name, vaccination_date, pet_id);