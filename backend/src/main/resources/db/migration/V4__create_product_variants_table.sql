CREATE TABLE product_variants (
    id               BIGSERIAL PRIMARY KEY,
    product_id       BIGINT NOT NULL REFERENCES products(id),
    size             VARCHAR(20) NOT NULL,
    color            VARCHAR(50) NOT NULL,
    color_hex        VARCHAR(7),
    price_adjustment DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock_quantity   INT NOT NULL DEFAULT 0,
    sku              VARCHAR(100) NOT NULL UNIQUE,
    image_url        VARCHAR(500),
    version          INT NOT NULL DEFAULT 0,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_product_size_color UNIQUE (product_id, size, color),
    CONSTRAINT chk_stock_non_negative CHECK (stock_quantity >= 0)
);
