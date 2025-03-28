CREATE USER  shopping_store WITH PASSWORD 'shopping_store';
CREATE USER  shopping_cart WITH PASSWORD 'shopping_cart';
CREATE USER  warehouse WITH PASSWORD 'warehouse';

CREATE SCHEMA AUTHORIZATION shopping_store;
CREATE SCHEMA AUTHORIZATION shopping_cart;
CREATE SCHEMA AUTHORIZATION warehouse;