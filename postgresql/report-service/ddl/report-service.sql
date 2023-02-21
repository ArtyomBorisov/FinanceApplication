CREATE USER "report-service_user" WITH PASSWORD 'CLlOg42MYtKt';
CREATE DATABASE "report_service" WITH OWNER = "report-service_user";
\c "report_service"

SET client_encoding = 'UTF8';

CREATE SCHEMA app;

ALTER SCHEMA app OWNER TO "report-service_user";

SET default_tablespace = '';

CREATE TABLE app.report (
    id uuid NOT NULL,
    dt_create timestamp without time zone NOT NULL,
    dt_update timestamp without time zone NOT NULL,
    status character varying NOT NULL,
    type character varying NOT NULL,
    description character varying NOT NULL,
    params text,
    "user" character varying NOT NULL
);

ALTER TABLE app.report OWNER TO "report-service_user";

ALTER TABLE ONLY app.report
    ADD CONSTRAINT report_pk PRIMARY KEY (id);

ALTER TABLE app.report
    ADD CONSTRAINT status_check CHECK (((status)::text = ANY ((ARRAY['LOADED'::character varying, 'PROGRESS'::character varying, 'ERROR'::character varying, 'DONE'::character varying])::text[]))) NOT VALID;

ALTER TABLE app.report
    ADD CONSTRAINT type_check CHECK (((type)::text = ANY ((ARRAY['BALANCE'::character varying, 'BY_DATE'::character varying, 'BY_CATEGORY'::character varying])::text[]))) NOT VALID;
