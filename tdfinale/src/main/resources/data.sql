-- Insertion de membres initiaux (INSERT)
INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date) 
VALUES ('M100', 'Jean', 'Dupont', '1985-05-15', 'MALE', '123 Rue de la Ferme', 'Agriculteur', 12345678, 'jean.dupont@agri.com', 'SENIOR', '2020-01-01');

INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date) 
VALUES ('M101', 'Marie', 'Curie', '1990-11-22', 'FEMALE', '456 Avenue des Champs', 'Chercheuse', 87654321, 'marie.curie@agri.com', 'SENIOR', '2021-06-12');

-- Insertion d'une collectivité (INSERT)
INSERT INTO collectivity (id, location, creation_date, president_id, vice_president_id, treasurer_id, secretary_id) 
VALUES ('C100', 'Antsirabe', '2023-01-01', 'M100', 'M101', 'M100', 'M101');

-- Relation collectivité-membres
INSERT INTO collectivity_members (collectivity_id, member_id) VALUES ('C100', 'M100');
INSERT INTO collectivity_members (collectivity_id, member_id) VALUES ('C100', 'M101');
