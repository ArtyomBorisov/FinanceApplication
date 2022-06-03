CREATE USER "${account.service.user}" WITH PASSWORD '${account.service.password}';
CREATE DATABASE "report-service" WITH OWNER = "${account.service.user}";
\c "report-service"

SET client_encoding = 'UTF8';

CREATE SCHEMA app;

ALTER SCHEMA app OWNER TO "${account.service.user}";

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

ALTER TABLE app.report OWNER TO "${account.service.user}";

CREATE TABLE app.report_file (
    id uuid NOT NULL,
    data bytea NOT NULL,
    "user" character varying NOT NULL
);

ALTER TABLE app.report_file OWNER TO "${account.service.user}";

ALTER TABLE ONLY app.report_file
    ADD CONSTRAINT report_file_pk PRIMARY KEY (id);

ALTER TABLE ONLY app.report
    ADD CONSTRAINT report_pk PRIMARY KEY (id);

ALTER TABLE app.report
    ADD CONSTRAINT status_check CHECK (((status)::text = ANY ((ARRAY['LOADED'::character varying, 'PROGRESS'::character varying, 'ERROR'::character varying, 'DONE'::character varying])::text[]))) NOT VALID;

ALTER TABLE app.report
    ADD CONSTRAINT type_check CHECK (((type)::text = ANY ((ARRAY['BALANCE'::character varying, 'BY_DATE'::character varying, 'BY_CATEGORY'::character varying])::text[]))) NOT VALID;
