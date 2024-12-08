CREATE SCHEMA IF NOT EXISTS billing;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS calls;

CREATE TABLE IF NOT EXISTS billing.clients (
  account_id serial PRIMARY KEY,
  dt_create timestamp without time zone DEFAULT now(),
  balance numeric(12,2) NOT NULL DEFAULT 0,
  is_blocked boolean NOT NULL DEFAULT false,
  name text
);

CREATE TABLE IF NOT EXISTS billing.pricelist
(
    id serial PRIMARY KEY,
    name text,
    date_from date NOT NULL,
    date_to date NOT NULL
);

CREATE TABLE IF NOT EXISTS billing.pricelist_item
(
    id serial,
    pricelist_id int NOT NULL,
    ndef bigint NOT NULL,    
    date_from date NOT NULL,
    date_to date NOT NULL,
    price numeric(8,4) NOT NULL,    
    CONSTRAINT defs_pkey PRIMARY KEY (ndef, pricelist_id)
);

CREATE TABLE IF NOT EXISTS billing.service_number
(
    id serial,
    account_id integer NOT NULL,
    did text NOT NULL,
    activation_dt timestamp without time zone NOT NULL,
    expire_dt timestamp without time zone,
    CONSTRAINT service_number_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS billing.service_trunk
(
    id serial,
    account_id integer NOT NULL,
    trunk_id integer NOT NULL,
    activation_dt timestamp without time zone NOT NULL,
    expire_dt timestamp without time zone,
    orig_enabled boolean NOT NULL DEFAULT false,
    term_enabled boolean NOT NULL DEFAULT false,
    CONSTRAINT service_trunk_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS auth.trunk
(
    id serial,
    route_table_id integer,
    trunk_name text,
    auth_by_number boolean NOT NULL DEFAULT false,
    CONSTRAINT trunk_idx PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS calls.cdr
(
    id bigserial,
    call_id bigint NOT NULL,
    src_number text,
    dst_number text,
    setup_time timestamp without time zone,
    connect_time timestamp without time zone,
    disconnect_time timestamp without time zone,
    session_time bigint,
    disconnect_cause smallint,
    src_route text,
    dst_route text,
    CONSTRAINT cdr_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS calls.raw
(
    id bigserial,
    orig boolean,
    peer_id bigint,
    cdr_id bigint,
    connect_time timestamp without time zone,
    trunk_id integer,
    account_id integer,
    trunk_service_id integer,
    number_service_id integer,
    src_number text,
    dst_number text,
    billed_time integer,
    rate numeric(12,2) NOT NULL DEFAULT 0,
    cost numeric(12,2) NOT NULL DEFAULT 0,
    pricelist_id integer,
    disconnect_cause smallint,
    trunk_settings_stats_id integer,
    CONSTRAINT calls_raw_pkey PRIMARY KEY (id)
);