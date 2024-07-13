create extension IF NOT EXISTS citext;
create extension IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS auths (
id bigserial primary key not null,
pass text not null,
email citext not null unique,
email_verification_code text not null,
is_email_verified boolean not null
);

CREATE TABLE IF NOT EXISTS tmp_test_employees(
  emp_id INTEGER PRIMARY KEY,
  age INTEGER NOT NULL,
  address VARCHAR(255),
  dateOfBirth VARCHAR(10)
);


CREATE TABLE IF NOT EXISTS tmp_test_table (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  price INTEGER NOT NULL,
  description TEXT
);

INSERT INTO tmp_test_table (name, price, description) VALUES
('Chair', 10, 'Description of chair'),
('Sofa', 20, 'Description of sofa'),
('Dresser', 30, 'Description of dresser');


