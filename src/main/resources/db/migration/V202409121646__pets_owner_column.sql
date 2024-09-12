-- Add the new column `pet_owner` to the `pets` table
ALTER TABLE pets
    ADD COLUMN pet_owner VARCHAR(50);