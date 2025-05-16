ALTER TABLE members ADD COLUMN IF NOT EXISTS username VARCHAR(255) UNIQUE;
ALTER TABLE members ADD COLUMN IF NOT EXISTS password VARCHAR(255);
CREATE TABLE IF NOT EXISTS member_roles (
    member_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (member_id, role),
    FOREIGN KEY (member_id) REFERENCES members(id)
    );