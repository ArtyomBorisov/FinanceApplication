CREATE USER "account-service_user" WITH PASSWORD 'TiP22r1nJyyu';
CREATE DATABASE "account_service" WITH OWNER = "account-service_user";
\c "account_service"

SET client_encoding = 'UTF8';

CREATE SCHEMA app;

ALTER SCHEMA app OWNER TO "account-service_user";

CREATE FUNCTION app.changing_balance() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	BEGIN
			IF (TG_OP = 'INSERT') THEN
				UPDATE app.balance SET ("sum", dt_update) = ("sum" + NEW.value, now())
                WHERE id = NEW.account;
				RETURN NEW;
			ELSIF (TG_OP = 'UPDATE') THEN
				UPDATE app.balance SET ("sum", dt_update) = ("sum" - OLD.value + NEW.value, now())
                WHERE id = NEW.account;
				RETURN NEW;
			ELSIF (TG_OP = 'DELETE') THEN
				UPDATE app.balance SET ("sum", dt_update) = ("sum" - OLD.value, now())
                WHERE id = OLD.account;
				RETURN OLD;
			END IF;
		END;
$$;


ALTER FUNCTION app.changing_balance() OWNER TO "account-service_user";

SET default_tablespace = '';

CREATE TABLE app.account (
    id uuid NOT NULL,
    dt_create timestamp(3) without time zone NOT NULL,
    dt_update timestamp(3) without time zone NOT NULL,
    title character varying NOT NULL,
    description character varying,
    type character varying NOT NULL,
    currency uuid NOT NULL,
    balance uuid NOT NULL,
    "user" character varying NOT NULL
);

ALTER TABLE app.account OWNER TO "account-service_user";

CREATE TABLE app.balance (
    id uuid NOT NULL,
    dt_update timestamp(3) without time zone NOT NULL,
    sum numeric(1000,2) NOT NULL
);

ALTER TABLE app.balance OWNER TO "account-service_user";

CREATE TABLE app.operation (
    id uuid NOT NULL,
    dt_create timestamp(3) without time zone NOT NULL,
    dt_update timestamp(3) without time zone NOT NULL,
    date timestamp(3) without time zone NOT NULL,
    description character varying,
    category uuid NOT NULL,
    value numeric(1000,2) NOT NULL,
    currency uuid NOT NULL,
    account uuid NOT NULL
);

ALTER TABLE app.operation OWNER TO "account-service_user";

ALTER TABLE ONLY app.account
    ADD CONSTRAINT account_pk PRIMARY KEY (id);

ALTER TABLE ONLY app.balance
    ADD CONSTRAINT balance_pk PRIMARY KEY (id);

ALTER TABLE ONLY app.operation
    ADD CONSTRAINT operation_pk PRIMARY KEY (id);

ALTER TABLE ONLY app.account
    ADD CONSTRAINT title_unique UNIQUE (title, "user");

ALTER TABLE app.account
    ADD CONSTRAINT type_check CHECK (((type)::text = ANY ((ARRAY['CASH'::character varying, 'BANK_ACCOUNT'::character varying, 'BANK_DEPOSIT'::character varying])::text[]))) NOT VALID;

CREATE TRIGGER account_trigger AFTER INSERT OR DELETE OR UPDATE ON app.operation FOR EACH ROW EXECUTE FUNCTION app.changing_balance();

ALTER TABLE ONLY app.operation
    ADD CONSTRAINT account_fk FOREIGN KEY (account) REFERENCES app.account(id);

ALTER TABLE ONLY app.account
    ADD CONSTRAINT balance_fk FOREIGN KEY (id) REFERENCES app.balance(id) NOT VALID;
