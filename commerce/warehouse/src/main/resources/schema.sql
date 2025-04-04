CREATE TABLE IF NOT EXISTS products (
  product_id UUID PRIMARY KEY,
  fragile BOOLEAN NOT NULL,
  width NUMERIC(8, 2) NOT NULL,
  height NUMERIC(8, 2) NOT NULL,
  depth NUMERIC(8, 2) NOT NULL,
  weight NUMERIC(8, 2) NOT NULL,
  quantity BIGINT NOT NULL
);