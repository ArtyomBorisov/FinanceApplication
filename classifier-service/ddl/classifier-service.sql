CREATE USER "${classifier.service.user}" WITH PASSWORD '${classifier.service.password}';
CREATE DATABASE "classifier_service" WITH OWNER = "${classifier.service.user}";
\c "classifier-service"

SET client_encoding = 'UTF8';

CREATE SCHEMA app;

ALTER SCHEMA app OWNER TO "${classifier.service.user}";

SET default_tablespace = '';

CREATE TABLE app.category (
    id uuid NOT NULL,
    dt_create timestamp(3) without time zone NOT NULL,
    dt_update timestamp(3) without time zone NOT NULL,
    title character varying NOT NULL
);

ALTER TABLE app.category OWNER TO "${classifier.service.user}";

CREATE TABLE app.currency (
    id uuid NOT NULL,
    dt_create timestamp(3) without time zone NOT NULL,
    dt_update timestamp(3) without time zone NOT NULL,
    title character varying NOT NULL,
    description character varying NOT NULL
);

ALTER TABLE app.currency OWNER TO "${classifier.service.user}";

ALTER TABLE ONLY app.category
    ADD CONSTRAINT category_pk PRIMARY KEY (id);

ALTER TABLE ONLY app.currency
    ADD CONSTRAINT currency_pk PRIMARY KEY (id);
