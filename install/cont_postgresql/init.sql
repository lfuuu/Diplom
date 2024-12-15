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
COMMENT ON COLUMN billing.clients.dt_create IS 'Время создания абонента';
COMMENT ON COLUMN billing.clients.balance IS 'Баланс лицевого счета на начало расчетного периода';
COMMENT ON COLUMN billing.clients.is_blocked IS 'ЛС заблокирован: Да/Нет';

CREATE TABLE IF NOT EXISTS billing.pricelist
(
    id serial PRIMARY KEY,
    name text,
    date_from date NOT NULL,
    date_to date,
    round_to_sec boolean DEFAULT false
);

COMMENT ON COLUMN billing.pricelist.id IS 'код прайслиста';
COMMENT ON COLUMN billing.pricelist.name IS 'имя прайслиста';
COMMENT ON COLUMN billing.pricelist.date_from IS 'Дата начала действия прайслиста';
COMMENT ON COLUMN billing.pricelist.date_to IS 'Дата окончания действия прайслиста';
COMMENT ON COLUMN billing.pricelist.round_to_sec IS 'режим округления  до миинут/до секунд';


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

COMMENT ON COLUMN calls.cdr.id IS 'код записи CDR звонка который прошел через узел связи';
COMMENT ON COLUMN calls.cdr.call_id IS 'id звонка присвоенный на коммутаторе';
COMMENT ON COLUMN calls.cdr.src_number IS 'Номер А';
COMMENT ON COLUMN calls.cdr.dst_number IS 'Номер B';
COMMENT ON COLUMN calls.cdr.setup_time IS 'Время маршутизации вызова';
COMMENT ON COLUMN calls.cdr.connect_time IS 'Время поднятия трубки вызываемым абонентом';
COMMENT ON COLUMN calls.cdr.disconnect_time IS 'Время окончания разговора';
COMMENT ON COLUMN calls.cdr.session_time IS 'Длительность разговора в секундах';
COMMENT ON COLUMN calls.cdr.disconnect_cause IS 'Причина завершения разговора';
COMMENT ON COLUMN calls.cdr.src_route IS 'Название оригинационного транка вызова';
COMMENT ON COLUMN calls.cdr.dst_route IS 'Название терминационного транка вызова';

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

COMMENT ON COLUMN calls.raw.id IS 'код плеча тарификации';
COMMENT ON COLUMN calls.raw.orig IS 'Это оригинационное или терминационное плечо';
COMMENT ON COLUMN calls.raw.peer_id IS 'Cсылка на второе плечо этого вызова. в этой же таблице';
COMMENT ON COLUMN calls.raw.cdr_id IS 'Cсылка на CDR-факт звонка, который тарифицируется плечами';
COMMENT ON COLUMN calls.raw.connect_time IS 'Время начала звонка';
COMMENT ON COLUMN calls.raw.trunk_id IS 'Транк, на который легло плечо вызова';
COMMENT ON COLUMN calls.raw.client_id IS 'Лицевой счет на который легло плечо вызова';
COMMENT ON COLUMN calls.raw.service_trunk_id IS 'Услуга "Транк" на которую легло плечо вызова, если плечо операторское';
COMMENT ON COLUMN calls.raw.service_trunk_id IS 'Услуга "Номер" на которую легло плечо вызова, если плечо рознечное';
COMMENT ON COLUMN calls.raw.src_number IS 'Номер А плеча';
COMMENT ON COLUMN calls.raw.dst_number IS 'Номер B плеча';
COMMENT ON COLUMN calls.raw.billed_time IS 'Тарифицированная длительность вызова. Может отличаться от фактической в зависимости от режима округления';
COMMENT ON COLUMN calls.raw.rate IS 'Цена минуты разговора для абонента/оператора';
COMMENT ON COLUMN calls.raw.cost IS 'Стоимость разговора для абонента/оператора';
COMMENT ON COLUMN calls.raw.pricelist_id IS 'Прайслист по которому было тарифицированно плечо';
COMMENT ON COLUMN calls.raw.disconnect_cause IS 'причина завершения вызова';

CREATE TABLE IF NOT EXISTS billing.packet
(
    id bigserial,
    service_trunk_id integer,
    service_number_id integer,
    activation_dt timestamp without time zone NOT NULL,
    expire_dt timestamp without time zone,    
    orig boolean,
    pricelist_id integer,
    CONSTRAINT packets_pkey PRIMARY KEY (id),
    CONSTRAINT fk_service_trunk_id FOREIGN KEY (service_trunk_id) REFERENCES billing.service_trunk (id) MATCH SIMPLE,
    CONSTRAINT fk_service_number_id FOREIGN KEY (service_number_id) REFERENCES billing.service_number (id) MATCH SIMPLE,
    CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist (id) MATCH SIMPLE
);

COMMENT ON COLUMN billing.packet.id IS 'код пакета';
COMMENT ON COLUMN billing.packet.service_trunk_id IS 'Привязка к Услуге Транк';
COMMENT ON COLUMN billing.packet.service_number_id IS 'Привязка к Услуге Номер';
COMMENT ON COLUMN billing.packet.activation_dt IS 'Время начала действия пакета';
COMMENT ON COLUMN billing.packet.expire_dt IS 'Время окончания действия пакета';
COMMENT ON COLUMN billing.packet.orig IS 'Для оригинации или терминации';
COMMENT ON COLUMN billing.packet.pricelist_id IS 'Код прайслиста пакета';

CREATE TABLE IF NOT EXISTS auth.user
(
    id bigserial,
    client_id integer,
    login text,
    password text,
    active boolean,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES billing.clients (id) MATCH SIMPLE
);

COMMENT ON COLUMN auth.user.id IS 'код пользователя';
COMMENT ON COLUMN auth.user.client_id IS 'код клиента связанного с пользователем';
COMMENT ON COLUMN auth.user.login IS 'логин пользователя в ЛК';
COMMENT ON COLUMN auth.user.password IS 'Пароль пользователя в MD5';
COMMENT ON COLUMN auth.user.active IS 'Включен ли пользователь?';
