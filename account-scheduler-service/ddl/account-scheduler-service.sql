CREATE USER "account-scheduler-service_user" WITH PASSWORD 'P6OaVGkh4TjU';
CREATE DATABASE "account_scheduler_service" WITH OWNER = "account-scheduler-service_user";
\c "account_scheduler_service"

SET client_encoding = 'UTF8';

CREATE SCHEMA app;

ALTER SCHEMA app OWNER TO "account-scheduler-service_user";

CREATE SCHEMA quartz;

ALTER SCHEMA quartz OWNER TO "account-scheduler-service_user";

SET default_tablespace = '';

CREATE TABLE app.scheduled_operation (
    id uuid NOT NULL,
    dt_create timestamp(3) without time zone NOT NULL,
    dt_update timestamp(3) without time zone NOT NULL,
    start_time timestamp without time zone,
    stop_time timestamp without time zone,
    "interval" bigint,
    time_unit character varying,
    account uuid NOT NULL,
    description text,
    value numeric(1000,2) NOT NULL,
    currency uuid NOT NULL,
    category uuid NOT NULL,
    "user" character varying NOT NULL
);

ALTER TABLE app.scheduled_operation OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);

ALTER TABLE quartz.qrtz_blob_triggers OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);

ALTER TABLE quartz.qrtz_calendars OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120) NOT NULL,
    time_zone_id character varying(80)
);

ALTER TABLE quartz.qrtz_cron_triggers OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent boolean,
    requests_recovery boolean
);

ALTER TABLE quartz.qrtz_fired_triggers OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable boolean NOT NULL,
    is_nonconcurrent boolean NOT NULL,
    is_update_data boolean NOT NULL,
    requests_recovery boolean NOT NULL,
    job_data bytea
);

ALTER TABLE quartz.qrtz_job_details OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);

ALTER TABLE quartz.qrtz_locks OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);

ALTER TABLE quartz.qrtz_paused_trigger_grps OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);

ALTER TABLE quartz.qrtz_scheduler_state OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);

ALTER TABLE quartz.qrtz_simple_triggers OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean
);

ALTER TABLE quartz.qrtz_simprop_triggers OWNER TO "account-scheduler-service_user";

CREATE TABLE quartz.qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea
);

ALTER TABLE quartz.qrtz_triggers OWNER TO "account-scheduler-service_user";

ALTER TABLE app.scheduled_operation
    ADD CONSTRAINT "interval" CHECK (("interval" > 0)) NOT VALID;

ALTER TABLE ONLY app.scheduled_operation
    ADD CONSTRAINT scheduled_operation_pk PRIMARY KEY (id);

ALTER TABLE app.scheduled_operation
    ADD CONSTRAINT time_unit_check CHECK (((time_unit)::text = ANY ((ARRAY['SECOND'::character varying, 'MINUTE'::character varying, 'HOUR'::character varying, 'DAY'::character varying, 'WEEK'::character varying, 'MONTH'::character varying, 'YEAR'::character varying])::text[]))) NOT VALID;

ALTER TABLE ONLY quartz.qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_calendars
    ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);

ALTER TABLE ONLY quartz.qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_fired_triggers
    ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);

ALTER TABLE ONLY quartz.qrtz_job_details
    ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);

ALTER TABLE ONLY quartz.qrtz_locks
    ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);

ALTER TABLE ONLY quartz.qrtz_paused_trigger_grps
    ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_scheduler_state
    ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);

ALTER TABLE ONLY quartz.qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON quartz.qrtz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);

CREATE INDEX idx_qrtz_ft_j_g ON quartz.qrtz_fired_triggers USING btree (sched_name, job_name, job_group);

CREATE INDEX idx_qrtz_ft_jg ON quartz.qrtz_fired_triggers USING btree (sched_name, job_group);

CREATE INDEX idx_qrtz_ft_t_g ON quartz.qrtz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);

CREATE INDEX idx_qrtz_ft_tg ON quartz.qrtz_fired_triggers USING btree (sched_name, trigger_group);

CREATE INDEX idx_qrtz_ft_trig_inst_name ON quartz.qrtz_fired_triggers USING btree (sched_name, instance_name);

CREATE INDEX idx_qrtz_j_grp ON quartz.qrtz_job_details USING btree (sched_name, job_group);

CREATE INDEX idx_qrtz_j_req_recovery ON quartz.qrtz_job_details USING btree (sched_name, requests_recovery);

CREATE INDEX idx_qrtz_t_c ON quartz.qrtz_triggers USING btree (sched_name, calendar_name);

CREATE INDEX idx_qrtz_t_g ON quartz.qrtz_triggers USING btree (sched_name, trigger_group);

CREATE INDEX idx_qrtz_t_j ON quartz.qrtz_triggers USING btree (sched_name, job_name, job_group);

CREATE INDEX idx_qrtz_t_jg ON quartz.qrtz_triggers USING btree (sched_name, job_group);

CREATE INDEX idx_qrtz_t_n_g_state ON quartz.qrtz_triggers USING btree (sched_name, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_n_state ON quartz.qrtz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_next_fire_time ON quartz.qrtz_triggers USING btree (sched_name, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_misfire ON quartz.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_st ON quartz.qrtz_triggers USING btree (sched_name, trigger_state, next_fire_time);

CREATE INDEX idx_qrtz_t_nft_st_misfire ON quartz.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON quartz.qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_t_state ON quartz.qrtz_triggers USING btree (sched_name, trigger_state);

ALTER TABLE ONLY quartz.qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES quartz.qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES quartz.qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_sched_name_trigger_name_trigger_group_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES quartz.qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_sched_name_trigger_name_trigger_grou_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES quartz.qrtz_triggers(sched_name, trigger_name, trigger_group);

ALTER TABLE ONLY quartz.qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_sched_name_job_name_job_group_fkey FOREIGN KEY (sched_name, job_name, job_group) REFERENCES quartz.qrtz_job_details(sched_name, job_name, job_group);
