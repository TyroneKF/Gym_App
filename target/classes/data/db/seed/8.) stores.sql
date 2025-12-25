-- #####################################################
-- Inserting BULK Non-Seed DATA
-- #####################################################
INSERT INTO stores 
(
	store_name
	
) VALUES

('Aldi'),
('Amazon'),
('ASDA'),
('Co-Op'),
('Costco'),
('Iceland'),
('Farm Foods'),
('Lidl'),
('M&S Food'),
('Morisssons'),
('Muscle Food'),
('MyProtein'),
('Ocado'),
('Sainsbury'),
('Tesco'),
('Vivo Life'),
('Waitrose')

ON DUPLICATE KEY UPDATE -- Incase of duplicate, ensures fields match correctly to new insert
    store_name = VALUES(store_name);