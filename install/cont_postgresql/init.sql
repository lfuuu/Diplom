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

COMMENT ON COLUMN billing.clients.id IS 'номер лицевого счета абонента или внешнего оператора Узла Связи';
COMMENT ON COLUMN billing.clients.balance IS 'Баланс лицевого счета на начало расчетного периода';
COMMENT ON COLUMN billing.clients.is_blocked IS 'ЛС заблокирован: Да/Нет';

CREATE TABLE IF NOT EXISTS billing.pricelist
(
    id serial PRIMARY KEY,
    name text,
    date_from date NOT NULL,
    date_to date
);

COMMENT ON COLUMN billing.pricelist.id IS 'код прайслиста';
COMMENT ON COLUMN billing.pricelist.name IS 'имя прайслиста';
COMMENT ON COLUMN billing.pricelist.date_from IS 'Дата начала действия прайслиста';
COMMENT ON COLUMN billing.pricelist.date_to IS 'Дата окончания действия прайслиста';


CREATE TABLE IF NOT EXISTS auth.trunk
(
    id serial,
    trunk_name text,
    auth_by_number boolean NOT NULL DEFAULT false,
    CONSTRAINT trunk_idx PRIMARY KEY (id)
);

COMMENT ON COLUMN auth.trunk.id IS 'код транка';
COMMENT ON COLUMN auth.trunk.trunk_name IS 'Имя транка';
COMMENT ON COLUMN auth.trunk.auth_by_number IS 'Режим тракна: абоненты на Ватс/внешний оператор';

CREATE TABLE IF NOT EXISTS billing.pricelist_item
(
    id serial,
    pricelist_id int NOT NULL,
    ndef bigint NOT NULL,    
    date_from date NOT NULL,
    date_to date,
    price numeric(8,4) NOT NULL,    
    CONSTRAINT defs_pkey PRIMARY KEY (ndef, pricelist_id),
    CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist (id) MATCH SIMPLE
);

COMMENT ON COLUMN billing.pricelist_item.id IS 'код элемента прайслиста';
COMMENT ON COLUMN billing.pricelist_item.pricelist_id IS 'Элемент принадлежит прайслисту';
COMMENT ON COLUMN billing.pricelist_item.ndef IS 'Префикс вматичивания: Например - 7495';
COMMENT ON COLUMN billing.pricelist_item.date_from IS 'Дата начала действия префикса';
COMMENT ON COLUMN billing.pricelist_item.date_to IS 'Дата окончания действия';
COMMENT ON COLUMN billing.pricelist_item.price IS 'Цена минуты разговора по префиксу';


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

COMMENT ON COLUMN billing.service_number.id IS 'Код услуги "Номер"';
COMMENT ON COLUMN billing.service_number.client_id IS 'принадлежит Лицевому Счету';
COMMENT ON COLUMN billing.service_number.did IS 'Абоенентский номер, Например: 79263321122';
COMMENT ON COLUMN billing.service_number.activation_dt IS 'Время начала действия услуги';
COMMENT ON COLUMN billing.service_number.expire_dt IS 'Время окончания действия услуги';

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

COMMENT ON COLUMN billing.service_trunk.id IS 'Код услуги "Транк"';
COMMENT ON COLUMN billing.service_trunk.client_id IS 'принадлежит Лицевому Счету';
COMMENT ON COLUMN billing.service_trunk.trunk_id IS 'Услуга привязана к транку';
COMMENT ON COLUMN billing.service_trunk.activation_dt IS 'Время начала действия услуги';
COMMENT ON COLUMN billing.service_trunk.expire_dt IS 'Время окончания действия услуги';
COMMENT ON COLUMN billing.service_trunk.orig_enabled IS 'может ли транк принимать звонки на Узел Связи';
COMMENT ON COLUMN billing.service_trunk.term_enabled IS 'может ли транк отправлять звонки на внешний узел связи';


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