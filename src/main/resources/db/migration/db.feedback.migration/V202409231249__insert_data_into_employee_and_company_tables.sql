INSERT INTO company (name) VALUES
('Friends'),
('Seinfeld');

-- Insert employees for Friends company (id=1)
INSERT INTO employee (first_name, last_name, role, company_id, employee_id, department) VALUES
('Rachel', 'Green', 'admin', 1, 101, 'CX'),
('Ross', 'Geller', 'manager', 1, 102, 'PRODUCT'),
('Monica', 'Geller', 'manager', 1, 103, 'PRODUCT'),
('Chandler', 'Bing', 'manager', 1, 104, 'IT'),
('Joey', 'Tribbiani', 'employee', 1, 105, 'PRODUCT'),
('Phoebe', 'Buffay', 'employee', 1, 106, 'CX'),
('Gunther', 'Central', 'employee', 1, 107, 'PRODUCT'),
('Janice', 'Hosenstein', 'employee', 1, 108, 'CX'),
('Mike', 'Hannigan', 'employee', 1, 109, 'PRODUCT'),
('David', 'Scientist', 'employee', 1, 110, 'CX'),
('Ben', 'Geller', 'employee', 1, 111, 'PRODUCT'),
('Carol', 'Willick', 'employee', 1, 112, 'CX'),
('Susan', 'Bunch', 'employee', 1, 113, 'PRODUCT'),
('Emily', 'Waltham', 'employee', 1, 114, 'PRODUCT'),
('Richard', 'Burke', 'employee', 1, 115, 'IT');

-- Insert employees for Seinfeld company (id=2)
INSERT INTO employee (first_name, last_name, role, company_id, employee_id, department) VALUES
('Jerry', 'Seinfeld', 'admin', 2, 201, 'IT'),
('George', 'Costanza', 'manager', 2, 202, 'PRODUCT'),
('Elaine', 'Benes', 'manager', 2, 203, 'IT'),
('Cosmo', 'Kramer', 'manager', 2, 204, 'PRODUCT'),
('Newman', 'Postman', 'employee', 2, 205, 'IT'),
('Frank', 'Costanza', 'employee', 2, 206, 'PRODUCT'),
('Estelle', 'Costanza', 'employee', 2, 207, 'IT'),
('David', 'Puddy', 'employee', 2, 208, 'IT'),
('Kenny', 'Bania', 'employee', 2, 209, 'PRODUCT'),
('Jackie', 'Chiles', 'employee', 2, 210, 'CX'),
('Sue', 'Ellen', 'employee', 2, 211, 'PRODUCT'),
('Morty', 'Seinfeld', 'employee', 2, 212, 'Retail'),
('Helen', 'Seinfeld', 'employee', 2, 213, 'PRODUCT'),
('Uncle', 'Leo', 'employee', 2, 214, 'CX'),
('Babu', 'Bhatt', 'employee', 2, 215, 'PRODUCT');
