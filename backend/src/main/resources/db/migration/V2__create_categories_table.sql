CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url   VARCHAR(500),
    parent_id   BIGINT REFERENCES categories(id),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
