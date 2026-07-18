CREATE TABLE orders (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id),
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount     DECIMAL(12,2) NOT NULL,
    shipping_address TEXT NOT NULL,
    payment_status   VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);
