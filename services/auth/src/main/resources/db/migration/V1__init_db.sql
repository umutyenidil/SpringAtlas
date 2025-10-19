CREATE TABLE auth
(
    id         UUID PRIMARY KEY,
    email   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    deleted_at TIMESTAMP
);