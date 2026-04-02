-- REGIONS (фиксируем ID!)
INSERT INTO regions (id, name) VALUES
                                   (1, 'South Tyrol, Italy'),
                                   (2, 'Valais, Switzerland'),
                                   (3, 'Carinthia, Austria'),
                                   (4, 'Salzburg, Austria'),
                                   (5, 'Bernese Highlands, Switzerland');

-- USERS
INSERT INTO users (id, name, email) VALUES
                                        (1, 'Anna Müller', 'anna@example.com'),
                                        (2, 'John Smith', 'john@example.com'),
                                        (3, 'Maria Rossi', 'maria@example.com');

-- FARMS
INSERT INTO farms (
    id, name, active, description, email, phone, established_year, region_id
) VALUES
      (1, 'Faltnerhof', true, 'Family farm', 'info@faltnerhof.com', '+39 349 7025607', 1890, 1),
      (2, 'Sennerei Grengiols', true, 'Dairy farm', NULL, '+41 27 927 17 11', 1927, 2),
      (3, 'Untere Bischofalm', true, 'Historic farm', 'bischof@aon.at', '+43 04715 319', 1743, 3),
      (4, 'Finkalm', true, 'Alpine hut', 'fink@sbg.at', '+43 664 514 76 11', 1807, 4),
      (5, 'Rinderalp', true, 'Swiss farm', 'godi@rinderalp.ch', '+41 33 681 27 68', 1977, 5);

-- ACTIVITIES
INSERT INTO activities (id, name) VALUES
                                      (1, 'Farm tour'),
                                      (2, 'Cheese tasting'),
                                      (3, 'Cheese production workshop'),
                                      (4, 'Animal care'),
                                      (5, 'Hiking');

-- M:N TABLE
INSERT INTO farm_activity (farm_id, activity_id) VALUES
                                                     (1,1),(1,2),(1,3),
                                                     (2,1),(2,2),
                                                     (3,1),(3,4),
                                                     (4,1),(4,5),
                                                     (5,2),(5,4);

-- ACCOMMODATIONS
INSERT INTO accommodations (id, type, price, farm_id) VALUES
                                                          (1, 'AGRITOURISM_ROOM', 120.0, 1),
                                                          (2, 'DAIRY_GUEST_ROOM', 95.0, 2),
                                                          (3, 'ALPINE_HUT', 110.0, 3),
                                                          (4, 'APARTMENT', 140.0, 4),
                                                          (5, 'LODGE', 100.0, 5);

-- BOOKINGS
INSERT INTO bookings (id, date, user_id, accommodation_id) VALUES
                                                               (1, '2026-06-10', 1, 1),
                                                               (2, '2026-06-12', 2, 3),
                                                               (3, '2026-06-15', 3, 5);