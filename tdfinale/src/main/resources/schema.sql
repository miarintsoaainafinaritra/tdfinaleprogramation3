CREATE TABLE member (
    id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    birth_date DATE,
    gender VARCHAR(10),
    address VARCHAR(255),
    profession VARCHAR(100),
    phone_number INT,
    email VARCHAR(100),
    occupation VARCHAR(20),
    adhesion_date DATE
);

CREATE TABLE collectivity (
    id VARCHAR(50) PRIMARY KEY,
    location VARCHAR(100),
    creation_date DATE,
    president_id VARCHAR(50),
    vice_president_id VARCHAR(50),
    treasurer_id VARCHAR(50),
    secretary_id VARCHAR(50),
    FOREIGN KEY (president_id) REFERENCES member(id),
    FOREIGN KEY (vice_president_id) REFERENCES member(id),
    FOREIGN KEY (treasurer_id) REFERENCES member(id),
    FOREIGN KEY (secretary_id) REFERENCES member(id)
);

CREATE TABLE collectivity_members (
    collectivity_id VARCHAR(50),
    member_id VARCHAR(50),
    PRIMARY KEY (collectivity_id, member_id),
    FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE member_referees (
    member_id VARCHAR(50),
    referee_id VARCHAR(50),
    PRIMARY KEY (member_id, referee_id),
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (referee_id) REFERENCES member(id)
);
