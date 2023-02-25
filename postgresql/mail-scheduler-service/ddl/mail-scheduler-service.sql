CREATE USER "mail-scheduler-service_user" WITH PASSWORD 'CLlOg42MYtKt';
CREATE DATABASE "mail_scheduler_service" WITH OWNER = "mail-scheduler-service_user";
\c "mail_scheduler_service"

SET client_encoding = 'UTF8';

CREATE SCHEMA app;

ALTER SCHEMA app OWNER TO "mail-scheduler-service_user";

CREATE TYPE app.report_type_enum AS ENUM (
    'BALANCE',
    'BY_DATE',
    'BY_CATEGORY'
);


ALTER TYPE app.report_type_enum OWNER TO "mail-scheduler-service_user";

CREATE TABLE app.monthly_report (
    login character varying NOT NULL,
    dt_create timestamp without time zone NOT NULL,
    report_type app.report_type_enum NOT NULL,
    email character varying NOT NULL,
    last_report character varying
);


ALTER TABLE app.monthly_report OWNER TO "mail-scheduler-service_user";

ALTER TABLE ONLY app.monthly_report
    ADD CONSTRAINT monthly_report_pkey PRIMARY KEY (login);
