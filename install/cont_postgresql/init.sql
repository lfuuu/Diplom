--
-- PostgreSQL database dump
--

-- Dumped from database version 12.22 (Debian 12.22-1.pgdg120+1)
-- Dumped by pg_dump version 14.17 (Ubuntu 14.17-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: auth; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA auth;


ALTER SCHEMA auth OWNER TO postgres;

--
-- Name: billing; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA billing;


ALTER SCHEMA billing OWNER TO postgres;

--
-- Name: calls; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA calls;


ALTER SCHEMA calls OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: trunk; Type: TABLE; Schema: auth; Owner: postgres
--

CREATE TABLE auth.trunk (
    id integer NOT NULL,
    trunk_name text,
    auth_by_number boolean DEFAULT false NOT NULL
);


ALTER TABLE auth.trunk OWNER TO postgres;

--
-- Name: COLUMN trunk.id; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth.trunk.id IS 'код транка';


--
-- Name: COLUMN trunk.trunk_name; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth.trunk.trunk_name IS 'Имя транка';


--
-- Name: COLUMN trunk.auth_by_number; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth.trunk.auth_by_number IS 'Режим тракна: абоненты на Ватс/внешний оператор';


--
-- Name: trunk_id_seq; Type: SEQUENCE; Schema: auth; Owner: postgres
--

CREATE SEQUENCE auth.trunk_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth.trunk_id_seq OWNER TO postgres;

--
-- Name: trunk_id_seq; Type: SEQUENCE OWNED BY; Schema: auth; Owner: postgres
--

ALTER SEQUENCE auth.trunk_id_seq OWNED BY auth.trunk.id;


--
-- Name: user; Type: TABLE; Schema: auth; Owner: postgres
--

CREATE TABLE auth."user" (
    id bigint NOT NULL,
    client_id integer,
    login text,
    password text,
    active boolean
);


ALTER TABLE auth."user" OWNER TO postgres;

--
-- Name: COLUMN "user".id; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".id IS 'код пользователя';


--
-- Name: COLUMN "user".client_id; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".client_id IS 'код клиента связанного с пользователем';


--
-- Name: COLUMN "user".login; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".login IS 'логин пользователя в ЛК';


--
-- Name: COLUMN "user".password; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".password IS 'Пароль пользователя в MD5';


--
-- Name: COLUMN "user".active; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".active IS 'Включен ли пользователь?';


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: auth; Owner: postgres
--

CREATE SEQUENCE auth.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth.user_id_seq OWNER TO postgres;

--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: auth; Owner: postgres
--

ALTER SEQUENCE auth.user_id_seq OWNED BY auth."user".id;


--
-- Name: clients; Type: TABLE; Schema: billing; Owner: postgres
--

CREATE TABLE billing.clients (
    id integer NOT NULL,
    dt_create timestamp with time zone DEFAULT now(),
    balance numeric(12,2) DEFAULT 0 NOT NULL,
    is_blocked boolean DEFAULT false NOT NULL,
    name text
);


ALTER TABLE billing.clients OWNER TO postgres;

--
-- Name: COLUMN clients.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.id IS 'номер лицевого счета абонента или внешнего оператора Узла Связи';


--
-- Name: COLUMN clients.dt_create; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.dt_create IS 'Время создания абонента';


--
-- Name: COLUMN clients.balance; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.balance IS 'Баланс лицевого счета на начало расчетного периода';


--
-- Name: COLUMN clients.is_blocked; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.is_blocked IS 'ЛС заблокирован: Да/Нет';


--
-- Name: clients_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.clients_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE billing.clients_id_seq OWNER TO postgres;

--
-- Name: clients_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.clients_id_seq OWNED BY billing.clients.id;


--
-- Name: packet; Type: TABLE; Schema: billing; Owner: postgres
--

CREATE TABLE billing.packet (
    id bigint NOT NULL,
    service_trunk_id integer,
    service_number_id integer,
    activation_dt timestamp with time zone NOT NULL,
    expire_dt timestamp with time zone,
    orig boolean,
    pricelist_id integer
);


ALTER TABLE billing.packet OWNER TO postgres;

--
-- Name: COLUMN packet.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.id IS 'код пакета';


--
-- Name: COLUMN packet.service_trunk_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.service_trunk_id IS 'Привязка к Услуге Транк';


--
-- Name: COLUMN packet.service_number_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.service_number_id IS 'Привязка к Услуге Номер';


--
-- Name: COLUMN packet.activation_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.activation_dt IS 'Время начала действия пакета';


--
-- Name: COLUMN packet.expire_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.expire_dt IS 'Время окончания действия пакета';


--
-- Name: COLUMN packet.orig; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.orig IS 'Для оригинации или терминации';


--
-- Name: COLUMN packet.pricelist_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.pricelist_id IS 'Код прайслиста пакета';


--
-- Name: packet_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.packet_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE billing.packet_id_seq OWNER TO postgres;

--
-- Name: packet_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.packet_id_seq OWNED BY billing.packet.id;


--
-- Name: pricelist; Type: TABLE; Schema: billing; Owner: postgres
--

CREATE TABLE billing.pricelist (
    id integer NOT NULL,
    name text,
    date_from date NOT NULL,
    date_to date,
    round_to_sec boolean DEFAULT false
);


ALTER TABLE billing.pricelist OWNER TO postgres;

--
-- Name: COLUMN pricelist.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.id IS 'код прайслиста';


--
-- Name: COLUMN pricelist.name; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.name IS 'имя прайслиста';


--
-- Name: COLUMN pricelist.date_from; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.date_from IS 'Дата начала действия прайслиста';


--
-- Name: COLUMN pricelist.date_to; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.date_to IS 'Дата окончания действия прайслиста';


--
-- Name: COLUMN pricelist.round_to_sec; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.round_to_sec IS 'режим округления  до миинут/до секунд';


--
-- Name: pricelist_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.pricelist_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE billing.pricelist_id_seq OWNER TO postgres;

--
-- Name: pricelist_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.pricelist_id_seq OWNED BY billing.pricelist.id;


--
-- Name: pricelist_item; Type: TABLE; Schema: billing; Owner: postgres
--

CREATE TABLE billing.pricelist_item (
    id integer NOT NULL,
    pricelist_id integer NOT NULL,
    ndef bigint NOT NULL,
    date_from date NOT NULL,
    date_to date,
    price numeric(8,4) NOT NULL
);


ALTER TABLE billing.pricelist_item OWNER TO postgres;

--
-- Name: COLUMN pricelist_item.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.id IS 'код элемента прайслиста';


--
-- Name: COLUMN pricelist_item.pricelist_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.pricelist_id IS 'Элемент принадлежит прайслисту';


--
-- Name: COLUMN pricelist_item.ndef; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.ndef IS 'Префикс вматичивания: Например - 7495';


--
-- Name: COLUMN pricelist_item.date_from; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.date_from IS 'Дата начала действия префикса';


--
-- Name: COLUMN pricelist_item.date_to; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.date_to IS 'Дата окончания действия';


--
-- Name: COLUMN pricelist_item.price; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.price IS 'Цена минуты разговора по префиксу';


--
-- Name: pricelist_item_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.pricelist_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE billing.pricelist_item_id_seq OWNER TO postgres;

--
-- Name: pricelist_item_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.pricelist_item_id_seq OWNED BY billing.pricelist_item.id;


--
-- Name: service_number; Type: TABLE; Schema: billing; Owner: postgres
--

CREATE TABLE billing.service_number (
    id integer NOT NULL,
    client_id integer NOT NULL,
    did text NOT NULL,
    activation_dt timestamp with time zone NOT NULL,
    expire_dt timestamp with time zone
);


ALTER TABLE billing.service_number OWNER TO postgres;

--
-- Name: COLUMN service_number.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.id IS 'Код услуги "Номер"';


--
-- Name: COLUMN service_number.client_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.client_id IS 'принадлежит Лицевому Счету';


--
-- Name: COLUMN service_number.did; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.did IS 'Абоенентский номер, Например: 79263321122';


--
-- Name: COLUMN service_number.activation_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.activation_dt IS 'Время начала действия услуги';


--
-- Name: COLUMN service_number.expire_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.expire_dt IS 'Время окончания действия услуги';


--
-- Name: service_number_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.service_number_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE billing.service_number_id_seq OWNER TO postgres;

--
-- Name: service_number_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.service_number_id_seq OWNED BY billing.service_number.id;


--
-- Name: service_trunk; Type: TABLE; Schema: billing; Owner: postgres
--

CREATE TABLE billing.service_trunk (
    id integer NOT NULL,
    client_id integer NOT NULL,
    trunk_id integer NOT NULL,
    activation_dt timestamp with time zone NOT NULL,
    expire_dt timestamp with time zone,
    orig_enabled boolean DEFAULT false NOT NULL,
    term_enabled boolean DEFAULT false NOT NULL
);


ALTER TABLE billing.service_trunk OWNER TO postgres;

--
-- Name: COLUMN service_trunk.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.id IS 'Код услуги "Транк"';


--
-- Name: COLUMN service_trunk.client_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.client_id IS 'принадлежит Лицевому Счету';


--
-- Name: COLUMN service_trunk.trunk_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.trunk_id IS 'Услуга привязана к транку';


--
-- Name: COLUMN service_trunk.activation_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.activation_dt IS 'Время начала действия услуги';


--
-- Name: COLUMN service_trunk.expire_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.expire_dt IS 'Время окончания действия услуги';


--
-- Name: COLUMN service_trunk.orig_enabled; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.orig_enabled IS 'может ли транк принимать звонки на Узел Связи';


--
-- Name: COLUMN service_trunk.term_enabled; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.term_enabled IS 'может ли транк отправлять звонки на внешний узел связи';


--
-- Name: service_trunk_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.service_trunk_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE billing.service_trunk_id_seq OWNER TO postgres;

--
-- Name: service_trunk_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.service_trunk_id_seq OWNED BY billing.service_trunk.id;


--
-- Name: cdr; Type: TABLE; Schema: calls; Owner: postgres
--

CREATE TABLE calls.cdr (
    id bigint NOT NULL,
    call_id bigint NOT NULL,
    src_number text,
    dst_number text,
    setup_time timestamp with time zone,
    connect_time timestamp with time zone,
    disconnect_time timestamp with time zone,
    session_time bigint,
    disconnect_cause smallint,
    src_route text,
    dst_route text
);


ALTER TABLE calls.cdr OWNER TO postgres;

--
-- Name: COLUMN cdr.id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.id IS 'код записи CDR звонка который прошел через узел связи';


--
-- Name: COLUMN cdr.call_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.call_id IS 'id звонка присвоенный на коммутаторе';


--
-- Name: COLUMN cdr.src_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.src_number IS 'Номер А';


--
-- Name: COLUMN cdr.dst_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.dst_number IS 'Номер B';


--
-- Name: COLUMN cdr.setup_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.setup_time IS 'Время маршутизации вызова';


--
-- Name: COLUMN cdr.connect_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.connect_time IS 'Время поднятия трубки вызываемым абонентом';


--
-- Name: COLUMN cdr.disconnect_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.disconnect_time IS 'Время окончания разговора';


--
-- Name: COLUMN cdr.session_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.session_time IS 'Длительность разговора в секундах';


--
-- Name: COLUMN cdr.disconnect_cause; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.disconnect_cause IS 'Причина завершения разговора';


--
-- Name: COLUMN cdr.src_route; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.src_route IS 'Название оригинационного транка вызова';


--
-- Name: COLUMN cdr.dst_route; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.dst_route IS 'Название терминационного транка вызова';


--
-- Name: cdr_id_seq; Type: SEQUENCE; Schema: calls; Owner: postgres
--

CREATE SEQUENCE calls.cdr_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE calls.cdr_id_seq OWNER TO postgres;

--
-- Name: cdr_id_seq; Type: SEQUENCE OWNED BY; Schema: calls; Owner: postgres
--

ALTER SEQUENCE calls.cdr_id_seq OWNED BY calls.cdr.id;


--
-- Name: raw; Type: TABLE; Schema: calls; Owner: postgres
--

CREATE TABLE calls.raw (
    id bigint NOT NULL,
    orig boolean,
    peer_id bigint,
    cdr_id bigint,
    connect_time timestamp with time zone,
    trunk_id integer,
    client_id integer,
    service_trunk_id integer,
    service_number_id integer,
    src_number text,
    dst_number text,
    billed_time integer,
    rate numeric(12,2) DEFAULT 0 NOT NULL,
    cost numeric(12,2) DEFAULT 0 NOT NULL,
    pricelist_id integer,
    disconnect_cause smallint
);


ALTER TABLE calls.raw OWNER TO postgres;

--
-- Name: COLUMN raw.id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.id IS 'код плеча тарификации';


--
-- Name: COLUMN raw.orig; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.orig IS 'Это оригинационное или терминационное плечо';


--
-- Name: COLUMN raw.peer_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.peer_id IS 'Cсылка на второе плечо этого вызова. в этой же таблице';


--
-- Name: COLUMN raw.cdr_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.cdr_id IS 'Cсылка на CDR-факт звонка, который тарифицируется плечами';


--
-- Name: COLUMN raw.connect_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.connect_time IS 'Время начала звонка';


--
-- Name: COLUMN raw.trunk_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.trunk_id IS 'Транк, на который легло плечо вызова';


--
-- Name: COLUMN raw.client_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.client_id IS 'Лицевой счет на который легло плечо вызова';


--
-- Name: COLUMN raw.service_trunk_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.service_trunk_id IS 'Услуга "Номер" на которую легло плечо вызова, если плечо рознечное';


--
-- Name: COLUMN raw.src_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.src_number IS 'Номер А плеча';


--
-- Name: COLUMN raw.dst_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.dst_number IS 'Номер B плеча';


--
-- Name: COLUMN raw.billed_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.billed_time IS 'Тарифицированная длительность вызова. Может отличаться от фактической в зависимости от режима округления';


--
-- Name: COLUMN raw.rate; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.rate IS 'Цена минуты разговора для абонента/оператора';


--
-- Name: COLUMN raw.cost; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.cost IS 'Стоимость разговора для абонента/оператора';


--
-- Name: COLUMN raw.pricelist_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.pricelist_id IS 'Прайслист по которому было тарифицированно плечо';


--
-- Name: COLUMN raw.disconnect_cause; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.disconnect_cause IS 'причина завершения вызова';


--
-- Name: raw_id_seq; Type: SEQUENCE; Schema: calls; Owner: postgres
--

CREATE SEQUENCE calls.raw_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE calls.raw_id_seq OWNER TO postgres;

--
-- Name: raw_id_seq; Type: SEQUENCE OWNED BY; Schema: calls; Owner: postgres
--

ALTER SEQUENCE calls.raw_id_seq OWNED BY calls.raw.id;


--
-- Name: trunk id; Type: DEFAULT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth.trunk ALTER COLUMN id SET DEFAULT nextval('auth.trunk_id_seq'::regclass);


--
-- Name: user id; Type: DEFAULT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth."user" ALTER COLUMN id SET DEFAULT nextval('auth.user_id_seq'::regclass);


--
-- Name: clients id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.clients ALTER COLUMN id SET DEFAULT nextval('billing.clients_id_seq'::regclass);


--
-- Name: packet id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet ALTER COLUMN id SET DEFAULT nextval('billing.packet_id_seq'::regclass);


--
-- Name: pricelist id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist ALTER COLUMN id SET DEFAULT nextval('billing.pricelist_id_seq'::regclass);


--
-- Name: pricelist_item id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist_item ALTER COLUMN id SET DEFAULT nextval('billing.pricelist_item_id_seq'::regclass);


--
-- Name: service_number id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_number ALTER COLUMN id SET DEFAULT nextval('billing.service_number_id_seq'::regclass);


--
-- Name: service_trunk id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk ALTER COLUMN id SET DEFAULT nextval('billing.service_trunk_id_seq'::regclass);


--
-- Name: cdr id; Type: DEFAULT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.cdr ALTER COLUMN id SET DEFAULT nextval('calls.cdr_id_seq'::regclass);


--
-- Name: raw id; Type: DEFAULT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw ALTER COLUMN id SET DEFAULT nextval('calls.raw_id_seq'::regclass);


--
-- Data for Name: trunk; Type: TABLE DATA; Schema: auth; Owner: postgres
--

COPY auth.trunk (id, trunk_name, auth_by_number) FROM stdin;
1	vpbx1	t
2	op1	f
3	op2	f
4	op3	f
\.


--
-- Data for Name: user; Type: TABLE DATA; Schema: auth; Owner: postgres
--

COPY auth."user" (id, client_id, login, password, active) FROM stdin;
1	1	client1	123	t
2	2	client2	123	t
3	3	client3	123	t
\.


--
-- Data for Name: clients; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.clients (id, dt_create, balance, is_blocked, name) FROM stdin;
1	2025-04-22 18:32:55.710499+00	100.00	f	Client N1
2	2025-04-22 18:32:58.852685+00	-2.00	t	Client N2
3	2025-04-28 17:15:09.595794+00	200.00	f	Client N3
4	2025-04-28 17:20:01.711636+00	40000.00	f	Op1
5	2025-04-28 17:20:01.711636+00	333333.00	f	Op2
6	2025-04-28 17:20:01.711636+00	4444.00	f	Op3
\.


--
-- Data for Name: packet; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.packet (id, service_trunk_id, service_number_id, activation_dt, expire_dt, orig, pricelist_id) FROM stdin;
2	1	\N	2001-01-01 00:00:00+00	\N	t	3
3	\N	4	2001-01-01 00:00:00+00	\N	t	3
5	2	\N	2001-01-01 00:00:00+00	\N	f	5
6	3	\N	2001-01-01 00:00:00+00	\N	f	6
\.


--
-- Data for Name: pricelist; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.pricelist (id, name, date_from, date_to, round_to_sec) FROM stdin;
1	розница n1	2020-01-01	\N	f
2	Розница n2	2021-01-01	\N	f
3	Операторский1	2020-01-01	\N	t
4	Операторский2	2019-01-01	\N	t
5	Операторский3	2019-06-06	\N	t
6	Операторкий4	2000-01-01	\N	t
\.


--
-- Data for Name: pricelist_item; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.pricelist_item (id, pricelist_id, ndef, date_from, date_to, price) FROM stdin;
1	1	7495	2020-01-01	\N	1.2000
4	1	7903	2020-01-02	\N	1.5000
5	3	7495	2001-01-01	\N	1.1100
6	3	7903	2001-01-02	\N	1.4000
7	3	7496	2001-01-01	\N	3.2500
8	5	7496	2020-01-01	\N	0.9000
11	6	7495	2020-01-01	\N	0.8000
12	3	7888	2000-01-01	\N	4.4400
13	6	7498	2000-01-01	\N	0.7500
14	3	7498	2000-01-01	\N	3.0100
\.


--
-- Data for Name: service_number; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.service_number (id, client_id, did, activation_dt, expire_dt) FROM stdin;
1	1	74951110001	2025-01-01 00:00:00+00	\N
2	1	74951110002	2025-01-01 00:00:00+00	20025-02-02 00:00:00+00
3	2	74952220001	2021-01-01 00:00:00+00	\N
4	3	74963330003	2022-01-01 00:00:00+00	\N
\.


--
-- Data for Name: service_trunk; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.service_trunk (id, client_id, trunk_id, activation_dt, expire_dt, orig_enabled, term_enabled) FROM stdin;
1	2	2	2021-01-01 00:00:00+00	\N	t	t
2	3	3	2020-04-04 00:00:00+00	\N	t	t
3	4	4	2020-02-02 00:00:00+00	\N	f	t
\.


--
-- Data for Name: cdr; Type: TABLE DATA; Schema: calls; Owner: postgres
--

COPY calls.cdr (id, call_id, src_number, dst_number, setup_time, connect_time, disconnect_time, session_time, disconnect_cause, src_route, dst_route) FROM stdin;
1	0	string	string	2025-05-05 19:04:00.427+00	2025-05-05 19:04:00.427+00	2025-05-05 19:04:00.427+00	0	0	string	string
2	0	string	string	2025-05-05 19:18:52.316+00	2025-05-05 19:18:52.316+00	2025-05-05 19:18:52.316+00	0	0	string	string
3	1234	74963330003	79261231111	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	vpbx1	op1
4	3321	74963330003	79031233344	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	vpbx1	op2
5	3321	79241233344	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	op1	vpbx1
6	3321	79162345666	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	op3	vpbx1
7	1234	74963330003	79261231111	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	vpbx1	op1
8	3321	74963330003	79031233344	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:42+00	41	16	vpbx1	op2
9	3321	79241233344	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:32+00	31	16	op1	vpbx1
10	3321	79162345666	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:02+00	0	31	op3	vpbx1
11	1234	74963330003	79261231111	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	vpbx1	op1
12	3321	74963330003	79031233344	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:42+00	41	16	vpbx1	op2
13	3321	79241233344	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:32+00	31	16	op1	vpbx1
14	3321	79162345666	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:02+00	0	31	op3	vpbx1
15	1234	74963330003	79261231111	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	vpbx1	op1
16	3321	74963330003	79031233344	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:42+00	41	16	vpbx1	op2
17	3321	79241233344	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:32+00	31	16	op1	vpbx1
18	3321	79162345666	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:02+00	0	31	op3	vpbx1
19	1234	74963330003	79261231111	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:52+00	51	16	vpbx1	op1
20	3321	74963330003	79031233344	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:42+00	41	16	vpbx1	op2
21	3321	79241233344	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:32+00	31	16	op1	vpbx1
22	3321	79162345666	74951110002	2025-05-05 01:01:01+00	2025-05-05 01:01:02+00	2025-05-05 01:01:02+00	0	31	op3	vpbx1
\.


--
-- Data for Name: raw; Type: TABLE DATA; Schema: calls; Owner: postgres
--

COPY calls.raw (id, orig, peer_id, cdr_id, connect_time, trunk_id, client_id, service_trunk_id, service_number_id, src_number, dst_number, billed_time, rate, cost, pricelist_id, disconnect_cause) FROM stdin;
\.


--
-- Name: trunk_id_seq; Type: SEQUENCE SET; Schema: auth; Owner: postgres
--

SELECT pg_catalog.setval('auth.trunk_id_seq', 4, true);


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: auth; Owner: postgres
--

SELECT pg_catalog.setval('auth.user_id_seq', 3, true);


--
-- Name: clients_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.clients_id_seq', 6, true);


--
-- Name: packet_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.packet_id_seq', 6, true);


--
-- Name: pricelist_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.pricelist_id_seq', 6, true);


--
-- Name: pricelist_item_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.pricelist_item_id_seq', 14, true);


--
-- Name: service_number_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.service_number_id_seq', 4, true);


--
-- Name: service_trunk_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.service_trunk_id_seq', 3, true);


--
-- Name: cdr_id_seq; Type: SEQUENCE SET; Schema: calls; Owner: postgres
--

SELECT pg_catalog.setval('calls.cdr_id_seq', 22, true);


--
-- Name: raw_id_seq; Type: SEQUENCE SET; Schema: calls; Owner: postgres
--

SELECT pg_catalog.setval('calls.raw_id_seq', 1, false);


--
-- Name: trunk trunk_idx; Type: CONSTRAINT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth.trunk
    ADD CONSTRAINT trunk_idx PRIMARY KEY (id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: clients clients_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);


--
-- Name: pricelist_item defs_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist_item
    ADD CONSTRAINT defs_pkey PRIMARY KEY (ndef, pricelist_id);


--
-- Name: packet packets_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT packets_pkey PRIMARY KEY (id);


--
-- Name: pricelist pricelist_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist
    ADD CONSTRAINT pricelist_pkey PRIMARY KEY (id);


--
-- Name: service_number service_number_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_number
    ADD CONSTRAINT service_number_pkey PRIMARY KEY (id);


--
-- Name: service_trunk service_trunk_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk
    ADD CONSTRAINT service_trunk_pkey PRIMARY KEY (id);


--
-- Name: raw calls_raw_pkey; Type: CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT calls_raw_pkey PRIMARY KEY (id);


--
-- Name: cdr cdr_pkey; Type: CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.cdr
    ADD CONSTRAINT cdr_pkey PRIMARY KEY (id);


--
-- Name: user fk_client_id; Type: FK CONSTRAINT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth."user"
    ADD CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- Name: service_number fk_clients_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_number
    ADD CONSTRAINT fk_clients_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- Name: service_trunk fk_clients_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk
    ADD CONSTRAINT fk_clients_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- Name: pricelist_item fk_pricelist_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist_item
    ADD CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist(id);


--
-- Name: packet fk_pricelist_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist(id);


--
-- Name: packet fk_service_number_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT fk_service_number_id FOREIGN KEY (service_number_id) REFERENCES billing.service_number(id);


--
-- Name: packet fk_service_trunk_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT fk_service_trunk_id FOREIGN KEY (service_trunk_id) REFERENCES billing.service_trunk(id);


--
-- Name: service_trunk fk_trunk_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk
    ADD CONSTRAINT fk_trunk_id FOREIGN KEY (trunk_id) REFERENCES auth.trunk(id);


--
-- Name: raw fk_cdr_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_cdr_id FOREIGN KEY (cdr_id) REFERENCES calls.cdr(id);


--
-- Name: raw fk_client_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- Name: raw fk_pricelist_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist(id);


--
-- Name: raw fk_service_number_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_service_number_id FOREIGN KEY (service_number_id) REFERENCES billing.service_number(id);


--
-- Name: raw fk_service_trunk_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_service_trunk_id FOREIGN KEY (service_trunk_id) REFERENCES billing.service_trunk(id);


--
-- Name: raw fk_trunk_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_trunk_id FOREIGN KEY (trunk_id) REFERENCES auth.trunk(id);


--
-- PostgreSQL database dump complete
--

