CREATE TABLE IF NOT EXISTS products (
  product_id UUID PRIMARY KEY,
  fragile BOOLEAN NOT NULL,
  width NUMERIC(8, 2) NOT NULL,
  height NUMERIC(8, 2) NOT NULL,
  depth NUMERIC(8, 2) NOT NULL,
  weight NUMERIC(8, 2) NOT NULL,
  quantity BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS consignments (
 consignment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
 order_id UUID NOT NULL,
 delivery_id UUID,
 state VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS consignments_products (
consignment_id UUID REFERENCES consignments(consignment_id) ON DELETE CASCADE,
product_id UUID REFERENCES products(product_id) ON DELETE RESTRICT,
quantity BIGINT NOT NULL,
PRIMARY KEY(consignment_id, product_id)
);