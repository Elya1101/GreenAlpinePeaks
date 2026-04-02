INSERT INTO regions (id, name) VALUES
                                   (1, 'South Tyrol, Italy'),
                                   (2, 'Valais, Switzerland'),
                                   (3, 'Carinthia, Austria'),
                                   (4, 'Salzburg, Austria'),
                                   (5, 'Bernese Highlands, Switzerland');

INSERT INTO users (id, name, email) VALUES
                                        (1, 'Anna Müller', 'anna@example.com'),
                                        (2, 'John Smith', 'john@example.com'),
                                        (3, 'Maria Rossi', 'maria@example.com');

INSERT INTO farms (
    id, name, active, description, email, phone, established_year, region_id
) VALUES
      (
          1,
          'Faltnerhof',
          true,
          'Family alpine farm producing cheese, butter and yogurt. Agritourism with Dolomites view.',
          'info@faltnerhof.com',
          '+39 349 7025607',
          1890,
          1
      ),
      (
          2,
          'Sennerei Grengiols',
          true,
          'Alpine cooperative dairy known for traditional hard cheeses from summer alpine milk.',
          NULL,
          '+41 27 927 17 11',
          1927,
          2
      ),
      (
          3,
          'Untere Bischofalm',
          true,
          'Historic high mountain farm producing cheese, butter and schnapps since 1743.',
          'bischof-gailtal@aon.at',
          '+43 04715 319',
          1743,
          3
      ),
      (
          4,
          'Finkalm',
          true,
          'Alpine hut offering food and guest accommodation after renovation in 2024.',
          'finkalm@sbg.at',
          '+43 664 514 76 11',
          1807,
          4
      ),
      (
          5,
          'Alpbeizli Rinderalp',
          true,
          'Swiss alpine farm offering cheese production and seasonal tourism.',
          'godi.knutti@rinderalp.ch',
          '+41 33 681 27 68',
          1977,
          5
      );

INSERT INTO activities (id, name) VALUES
                                      (1, 'Farm tour'),
                                      (2, 'Cheese tasting'),
                                      (3, 'Cheese production workshop'),
                                      (4, 'Animal care'),
                                      (5, 'Hiking'),
                                      (6, 'Alpine trekking'),
                                      (7, 'E-bike rental'),
                                      (8, 'Skiing'),
                                      (9, 'Snowshoe hiking'),
                                      (10, 'Schnapps tasting'),
                                      (11, 'Fondue tasting'),
                                      (12, 'Farm product purchase');

INSERT INTO farm_activity (farm_id, activity_id) VALUES
-- Faltnerhof
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,12),

-- Sennerei Grengiols
(2,1),(2,2),(2,3),(2,12),

-- Untere Bischofalm
(3,1),(3,2),(3,4),(3,10),(3,5),(3,6),(3,11),

-- Finkalm
(4,1),(4,2),(4,5),(4,6),(4,7),(4,8),(4,9),(4,10),(4,11),

-- Rinderalp
(5,1),(5,2),(5,4),(5,5),(5,6),(5,7),(5,11),(5,12);

INSERT INTO accommodations (id, type, price, farm_id) VALUES
                                                          (1, 'AGRITOURISM_ROOM', 120.0, 1),
                                                          (2, 'DAIRY_GUEST_ROOM', 95.0, 2),
                                                          (3, 'ALPINE_HUT', 110.0, 3),
                                                          (4, 'APARTMENT', 140.0, 4),
                                                          (5, 'LODGE', 100.0, 5);

INSERT INTO bookings (id, date, user_id, accommodation_id) VALUES
                                                               (1, '2026-06-10', 1, 1),
                                                               (2, '2026-06-12', 2, 3),
                                                               (3, '2026-06-15', 3, 5);