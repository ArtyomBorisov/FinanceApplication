CREATE USER "user-service_user" WITH PASSWORD 'K5Gdjpd34Fo0';
CREATE DATABASE "user_service" WITH OWNER = "user-service_user";
\c "user_service"

SET client_encoding = 'UTF8';

CREATE SCHEMA security;

ALTER SCHEMA security OWNER TO "user-service_user";

SET default_tablespace = '';

CREATE TABLE security.authorities (
    username character varying NOT NULL,
    authority character varying NOT NULL
);

ALTER TABLE security.authorities OWNER TO "user-service_user";

CREATE TABLE security.users (
    username character varying NOT NULL,
    password character varying NOT NULL,
    enabled boolean NOT NULL
);

ALTER TABLE security.users OWNER TO "user-service_user";

ALTER TABLE ONLY security.users
    ADD CONSTRAINT user_pk PRIMARY KEY (username);

ALTER TABLE ONLY security.authorities
    ADD CONSTRAINT username_fk FOREIGN KEY (username) REFERENCES security.users(username);
