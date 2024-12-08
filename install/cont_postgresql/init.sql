DROP SCHEMA IF EXISTS auth CASCADE;
DROP SCHEMA IF EXISTS billing CASCADE;
DROP SCHEMA IF EXISTS calls CASCADE;

CREATE SCHEMA IF NOT EXISTS billing;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS calls;

CREATE TABLE IF NOT EXISTS billing.clients (
  id serial PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS auth.trunk
(
    id serial,
    trunk_name text,
    auth_by_number boolean NOT NULL DEFAULT false,
    CONSTRAINT trunk_idx PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS billing.pricelist_item
(
    id serial,
    pricelist_id int NOT NULL,
    ndef bigint NOT NULL,    
    date_from date NOT NULL,
    date_to date NOT NULL,
    price numeric(8,4) NOT NULL,    
    CONSTRAINT defs_pkey PRIMARY KEY (ndef, pricelist_id),
    CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist (id) MATCH SIMPLE
);

CREATE TABLE IF NOT EXISTS billing.service_number
(
    id serial,
    client_id integer NOT NULL,
    did text NOT NULL,
    activation_dt timestamp without time zone NOT NULL,
    expire_dt timestamp without time zone,
    CONSTRAINT service_number_pkey PRIMARY KEY (id),
    CONSTRAINT fk_clients_id FOREIGN KEY (client_id) REFERENCES billing.clients (id) MATCH SIMPLE
);

CREATE TABLE IF NOT EXISTS billing.service_trunk
(
    id serial,
    client_id integer NOT NULL,
    trunk_id integer NOT NULL,
    activation_dt timestamp without time zone NOT NULL,
    expire_dt timestamp without time zone,
    orig_enabled boolean NOT NULL DEFAULT false,
    term_enabled boolean NOT NULL DEFAULT false,
    CONSTRAINT service_trunk_pkey PRIMARY KEY (id),
    CONSTRAINT fk_clients_id FOREIGN KEY (client_id) REFERENCES billing.clients (id) MATCH SIMPLE,
    CONSTRAINT fk_trunk_id FOREIGN KEY (trunk_id) REFERENCES auth.trunk (id) MATCH SIMPLE
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
    client_id integer,
    service_trunk_id integer,
    service_number_id integer,
    src_number text,
    dst_number text,
    billed_time integer,
    rate numeric(12,2) NOT NULL DEFAULT 0,
    cost numeric(12,2) NOT NULL DEFAULT 0,
    pricelist_id integer,
    disconnect_cause smallint,
    CONSTRAINT calls_raw_pkey PRIMARY KEY (id),
    CONSTRAINT fk_cdr_id FOREIGN KEY (cdr_id) REFERENCES calls.cdr (id) MATCH SIMPLE,
    CONSTRAINT fk_trunk_id FOREIGN KEY (trunk_id) REFERENCES auth.trunk (id) MATCH SIMPLE,
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES billing.clients (id) MATCH SIMPLE,
    CONSTRAINT fk_service_trunk_id FOREIGN KEY (service_trunk_id) REFERENCES billing.service_trunk (id) MATCH SIMPLE,
    CONSTRAINT fk_service_number_id FOREIGN KEY (service_number_id) REFERENCES billing.service_number (id) MATCH SIMPLE,
    CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist (id) MATCH SIMPLE
);