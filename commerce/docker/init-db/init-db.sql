CREATE USER  shopping_store WITH PASSWORD 'shopping_store';
CREATE USER  shopping_cart WITH PASSWORD 'shopping_cart';
CREATE USER  warehouse WITH PASSWORD 'warehouse';
CREATE USER  order_service WITH PASSWORD 'order_service';
CREATE USER  payment WITH PASSWORD 'payment';
CREATE USER  delivery WITH PASSWORD 'delivery';

CREATE SCHEMA AUTHORIZATION shopping_store;
CREATE SCHEMA AUTHORIZATION shopping_cart;
CREATE SCHEMA AUTHORIZATION warehouse;
CREATE SCHEMA AUTHORIZATION order_service;
CREATE SCHEMA AUTHORIZATION payment;
CREATE SCHEMA AUTHORIZATION delivery;