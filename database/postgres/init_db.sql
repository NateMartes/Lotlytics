-- Lotlyics Database Schema for Lots and Groups

CREATE TABLE groups (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE lots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    group_id VARCHAR(255) NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    capacity INT NOT NULL,
    current_volume INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
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

INSERT INTO lots (name, group_id, capacity, current_volume) VALUES 
('Lot A', 'wilkes-1a2b3c4d', 50, 10), 
('Lot B', 'wilkes-1a2b3c4d', 100, 75), 
('Lot C', 'google-5e6f7g8h', 30, 5), 
('Lot D', 'datadog-9i0j1k2l', 80, 40);
