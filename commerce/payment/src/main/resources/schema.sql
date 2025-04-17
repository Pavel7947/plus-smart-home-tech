CREATE TABLE IF NOT EXISTS payments (
  payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL,
  product_cost NUMERIC(11, 2) NOT NULL,
  delivery_cost NUMERIC(11, 2) NOT NULL,
  total_cost NUMERIC(11, 2) NOT NULL,
  state VARCHAR(50)
);