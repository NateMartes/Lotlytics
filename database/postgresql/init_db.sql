-- Lotlyics Database Schema for Lots and Groups

CREATE TABLE groups (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE lots (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    capacity INT NOT NULL,
    current_volume INT DEFAULT 0,
    name VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    zip VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE group_members (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) DEFAULT 'member',
    UNIQUE (group_id, user_id)
);

-- Trigger for auto-updating 'updated_at'
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
   RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER update_lot_updated_at
BEFORE UPDATE ON lots
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


-- REMOVE THIS IN THE FUTURE

INSERT INTO groups (id, name) VALUES 
('wilkes-1a2b3c4d', 'wilkes'), 
('google-5e6f7g8h', 'google'), 
('datadog-9i0j1k2l', 'datadog');

INSERT INTO lots (group_id, capacity, current_volume, name, street, city, state, zip) VALUES
('wilkes-1a2b3c4d', 50, 10, 'Lot A', '123 Main St', 'Wilkes-Barre', 'PA', '18711'),
('wilkes-1a2b3c4d', 100, 75, 'Lot B', '456 Maple Ave', 'Wilkes-Barre', 'PA', '18711'),
('google-5e6f7g8h', 30, 5, 'Lot C', '1600 Amphitheatre Pkwy', 'Mountain View', 'CA', '94043'),
('datadog-9i0j1k2l', 80, 40, 'Lot D', '500 Tech Dr', 'Boston', 'MA', '02110');
