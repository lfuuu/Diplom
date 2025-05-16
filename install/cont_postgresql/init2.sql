--
-- PostgreSQL database dump
--

-- Dumped from database version 12.22 (Debian 12.22-1.pgdg120+1)
-- Dumped by pg_dump version 17.4

-- Started on 2025-04-28 17:46:57 UTC

-- TOC entry 9 (class 2615 OID 16386)
-- Name: auth; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA auth;


ALTER SCHEMA auth OWNER TO postgres;

--
-- TOC entry 8 (class 2615 OID 16385)
-- Name: billing; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA billing;


ALTER SCHEMA billing OWNER TO postgres;

--
-- TOC entry 10 (class 2615 OID 16387)
-- Name: calls; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA calls;


ALTER SCHEMA calls OWNER TO postgres;

--
-- TOC entry 6 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 210 (class 1259 OID 16416)
-- Name: trunk; Type: TABLE; Schema: auth; Owner: postgres
--

CREATE TABLE auth.trunk (
    id integer NOT NULL,
    trunk_name text,
    auth_by_number boolean DEFAULT false NOT NULL
);


ALTER TABLE auth.trunk OWNER TO postgres;

--
-- TOC entry 3119 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN trunk.id; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth.trunk.id IS 'код транка';


--
-- TOC entry 3120 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN trunk.trunk_name; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth.trunk.trunk_name IS 'Имя транка';


--
-- TOC entry 3121 (class 0 OID 0)
-- Dependencies: 210
-- Name: COLUMN trunk.auth_by_number; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth.trunk.auth_by_number IS 'Режим тракна: абоненты на Ватс/внешний оператор';


--
-- TOC entry 209 (class 1259 OID 16414)
-- Name: trunk_id_seq; Type: SEQUENCE; Schema: auth; Owner: postgres
--

CREATE SEQUENCE auth.trunk_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE auth.trunk_id_seq OWNER TO postgres;

--
-- TOC entry 3122 (class 0 OID 0)
-- Dependencies: 209
-- Name: trunk_id_seq; Type: SEQUENCE OWNED BY; Schema: auth; Owner: postgres
--

ALTER SEQUENCE auth.trunk_id_seq OWNED BY auth.trunk.id;


--
-- TOC entry 224 (class 1259 OID 16554)
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
-- TOC entry 3123 (class 0 OID 0)
-- Dependencies: 224
-- Name: COLUMN "user".id; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".id IS 'код пользователя';


--
-- TOC entry 3124 (class 0 OID 0)
-- Dependencies: 224
-- Name: COLUMN "user".client_id; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".client_id IS 'код клиента связанного с пользователем';


--
-- TOC entry 3125 (class 0 OID 0)
-- Dependencies: 224
-- Name: COLUMN "user".login; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".login IS 'логин пользователя в ЛК';


--
-- TOC entry 3126 (class 0 OID 0)
-- Dependencies: 224
-- Name: COLUMN "user".password; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".password IS 'Пароль пользователя в MD5';


--
-- TOC entry 3127 (class 0 OID 0)
-- Dependencies: 224
-- Name: COLUMN "user".active; Type: COMMENT; Schema: auth; Owner: postgres
--

COMMENT ON COLUMN auth."user".active IS 'Включен ли пользователь?';


--
-- TOC entry 223 (class 1259 OID 16552)
-- Name: user_id_seq; Type: SEQUENCE; Schema: auth; Owner: postgres
--

CREATE SEQUENCE auth.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE auth.user_id_seq OWNER TO postgres;

--
-- TOC entry 3128 (class 0 OID 0)
-- Dependencies: 223
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: auth; Owner: postgres
--

ALTER SEQUENCE auth.user_id_seq OWNED BY auth."user".id;


--
-- TOC entry 206 (class 1259 OID 16390)
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
-- TOC entry 3129 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN clients.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.id IS 'номер лицевого счета абонента или внешнего оператора Узла Связи';


--
-- TOC entry 3130 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN clients.dt_create; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.dt_create IS 'Время создания абонента';


--
-- TOC entry 3131 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN clients.balance; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.balance IS 'Баланс лицевого счета на начало расчетного периода';


--
-- TOC entry 3132 (class 0 OID 0)
-- Dependencies: 206
-- Name: COLUMN clients.is_blocked; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.clients.is_blocked IS 'ЛС заблокирован: Да/Нет';


--
-- TOC entry 205 (class 1259 OID 16388)
-- Name: clients_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.clients_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE billing.clients_id_seq OWNER TO postgres;

--
-- TOC entry 3133 (class 0 OID 0)
-- Dependencies: 205
-- Name: clients_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.clients_id_seq OWNED BY billing.clients.id;


--
-- TOC entry 222 (class 1259 OID 16531)
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
-- TOC entry 3134 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.id IS 'код пакета';


--
-- TOC entry 3135 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.service_trunk_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.service_trunk_id IS 'Привязка к Услуге Транк';


--
-- TOC entry 3136 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.service_number_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.service_number_id IS 'Привязка к Услуге Номер';


--
-- TOC entry 3137 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.activation_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.activation_dt IS 'Время начала действия пакета';


--
-- TOC entry 3138 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.expire_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.expire_dt IS 'Время окончания действия пакета';


--
-- TOC entry 3139 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.orig; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.orig IS 'Для оригинации или терминации';


--
-- TOC entry 3140 (class 0 OID 0)
-- Dependencies: 222
-- Name: COLUMN packet.pricelist_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.packet.pricelist_id IS 'Код прайслиста пакета';


--
-- TOC entry 221 (class 1259 OID 16529)
-- Name: packet_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.packet_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE billing.packet_id_seq OWNER TO postgres;

--
-- TOC entry 3141 (class 0 OID 0)
-- Dependencies: 221
-- Name: packet_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.packet_id_seq OWNED BY billing.packet.id;


--
-- TOC entry 208 (class 1259 OID 16404)
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
-- TOC entry 3142 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN pricelist.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.id IS 'код прайслиста';


--
-- TOC entry 3143 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN pricelist.name; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.name IS 'имя прайслиста';


--
-- TOC entry 3144 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN pricelist.date_from; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.date_from IS 'Дата начала действия прайслиста';


--
-- TOC entry 3145 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN pricelist.date_to; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.date_to IS 'Дата окончания действия прайслиста';


--
-- TOC entry 3146 (class 0 OID 0)
-- Dependencies: 208
-- Name: COLUMN pricelist.round_to_sec; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist.round_to_sec IS 'режим округления  до миинут/до секунд';


--
-- TOC entry 207 (class 1259 OID 16402)
-- Name: pricelist_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.pricelist_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE billing.pricelist_id_seq OWNER TO postgres;

--
-- TOC entry 3147 (class 0 OID 0)
-- Dependencies: 207
-- Name: pricelist_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.pricelist_id_seq OWNED BY billing.pricelist.id;


--
-- TOC entry 212 (class 1259 OID 16428)
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
-- TOC entry 3148 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN pricelist_item.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.id IS 'код элемента прайслиста';


--
-- TOC entry 3149 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN pricelist_item.pricelist_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.pricelist_id IS 'Элемент принадлежит прайслисту';


--
-- TOC entry 3150 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN pricelist_item.ndef; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.ndef IS 'Префикс вматичивания: Например - 7495';


--
-- TOC entry 3151 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN pricelist_item.date_from; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.date_from IS 'Дата начала действия префикса';


--
-- TOC entry 3152 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN pricelist_item.date_to; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.date_to IS 'Дата окончания действия';


--
-- TOC entry 3153 (class 0 OID 0)
-- Dependencies: 212
-- Name: COLUMN pricelist_item.price; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.pricelist_item.price IS 'Цена минуты разговора по префиксу';


--
-- TOC entry 211 (class 1259 OID 16426)
-- Name: pricelist_item_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.pricelist_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE billing.pricelist_item_id_seq OWNER TO postgres;

--
-- TOC entry 3154 (class 0 OID 0)
-- Dependencies: 211
-- Name: pricelist_item_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.pricelist_item_id_seq OWNED BY billing.pricelist_item.id;


--
-- TOC entry 214 (class 1259 OID 16441)
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
-- TOC entry 3155 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN service_number.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.id IS 'Код услуги "Номер"';


--
-- TOC entry 3156 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN service_number.client_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.client_id IS 'принадлежит Лицевому Счету';


--
-- TOC entry 3157 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN service_number.did; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.did IS 'Абоенентский номер, Например: 79263321122';


--
-- TOC entry 3158 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN service_number.activation_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.activation_dt IS 'Время начала действия услуги';


--
-- TOC entry 3159 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN service_number.expire_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_number.expire_dt IS 'Время окончания действия услуги';


--
-- TOC entry 213 (class 1259 OID 16439)
-- Name: service_number_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.service_number_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE billing.service_number_id_seq OWNER TO postgres;

--
-- TOC entry 3160 (class 0 OID 0)
-- Dependencies: 213
-- Name: service_number_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.service_number_id_seq OWNED BY billing.service_number.id;


--
-- TOC entry 216 (class 1259 OID 16457)
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
-- TOC entry 3161 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.id IS 'Код услуги "Транк"';


--
-- TOC entry 3162 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.client_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.client_id IS 'принадлежит Лицевому Счету';


--
-- TOC entry 3163 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.trunk_id; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.trunk_id IS 'Услуга привязана к транку';


--
-- TOC entry 3164 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.activation_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.activation_dt IS 'Время начала действия услуги';


--
-- TOC entry 3165 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.expire_dt; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.expire_dt IS 'Время окончания действия услуги';


--
-- TOC entry 3166 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.orig_enabled; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.orig_enabled IS 'может ли транк принимать звонки на Узел Связи';


--
-- TOC entry 3167 (class 0 OID 0)
-- Dependencies: 216
-- Name: COLUMN service_trunk.term_enabled; Type: COMMENT; Schema: billing; Owner: postgres
--

COMMENT ON COLUMN billing.service_trunk.term_enabled IS 'может ли транк отправлять звонки на внешний узел связи';


--
-- TOC entry 215 (class 1259 OID 16455)
-- Name: service_trunk_id_seq; Type: SEQUENCE; Schema: billing; Owner: postgres
--

CREATE SEQUENCE billing.service_trunk_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE billing.service_trunk_id_seq OWNER TO postgres;

--
-- TOC entry 3168 (class 0 OID 0)
-- Dependencies: 215
-- Name: service_trunk_id_seq; Type: SEQUENCE OWNED BY; Schema: billing; Owner: postgres
--

ALTER SEQUENCE billing.service_trunk_id_seq OWNED BY billing.service_trunk.id;


--
-- TOC entry 218 (class 1259 OID 16477)
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
-- TOC entry 3169 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.id IS 'код записи CDR звонка который прошел через узел связи';


--
-- TOC entry 3170 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.call_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.call_id IS 'id звонка присвоенный на коммутаторе';


--
-- TOC entry 3171 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.src_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.src_number IS 'Номер А';


--
-- TOC entry 3172 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.dst_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.dst_number IS 'Номер B';


--
-- TOC entry 3173 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.setup_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.setup_time IS 'Время маршутизации вызова';


--
-- TOC entry 3174 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.connect_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.connect_time IS 'Время поднятия трубки вызываемым абонентом';


--
-- TOC entry 3175 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.disconnect_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.disconnect_time IS 'Время окончания разговора';


--
-- TOC entry 3176 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.session_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.session_time IS 'Длительность разговора в секундах';


--
-- TOC entry 3177 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.disconnect_cause; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.disconnect_cause IS 'Причина завершения разговора';


--
-- TOC entry 3178 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.src_route; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.src_route IS 'Название оригинационного транка вызова';


--
-- TOC entry 3179 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN cdr.dst_route; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.cdr.dst_route IS 'Название терминационного транка вызова';


--
-- TOC entry 217 (class 1259 OID 16475)
-- Name: cdr_id_seq; Type: SEQUENCE; Schema: calls; Owner: postgres
--

CREATE SEQUENCE calls.cdr_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE calls.cdr_id_seq OWNER TO postgres;

--
-- TOC entry 3180 (class 0 OID 0)
-- Dependencies: 217
-- Name: cdr_id_seq; Type: SEQUENCE OWNED BY; Schema: calls; Owner: postgres
--

ALTER SEQUENCE calls.cdr_id_seq OWNED BY calls.cdr.id;


--
-- TOC entry 220 (class 1259 OID 16488)
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
-- TOC entry 3181 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.id IS 'код плеча тарификации';


--
-- TOC entry 3182 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.orig; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.orig IS 'Это оригинационное или терминационное плечо';


--
-- TOC entry 3183 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.peer_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.peer_id IS 'Cсылка на второе плечо этого вызова. в этой же таблице';


--
-- TOC entry 3184 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.cdr_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.cdr_id IS 'Cсылка на CDR-факт звонка, который тарифицируется плечами';


--
-- TOC entry 3185 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.connect_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.connect_time IS 'Время начала звонка';


--
-- TOC entry 3186 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.trunk_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.trunk_id IS 'Транк, на который легло плечо вызова';


--
-- TOC entry 3187 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.client_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.client_id IS 'Лицевой счет на который легло плечо вызова';


--
-- TOC entry 3188 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.service_trunk_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.service_trunk_id IS 'Услуга "Номер" на которую легло плечо вызова, если плечо рознечное';


--
-- TOC entry 3189 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.src_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.src_number IS 'Номер А плеча';


--
-- TOC entry 3190 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.dst_number; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.dst_number IS 'Номер B плеча';


--
-- TOC entry 3191 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.billed_time; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.billed_time IS 'Тарифицированная длительность вызова. Может отличаться от фактической в зависимости от режима округления';


--
-- TOC entry 3192 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.rate; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.rate IS 'Цена минуты разговора для абонента/оператора';


--
-- TOC entry 3193 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.cost; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.cost IS 'Стоимость разговора для абонента/оператора';


--
-- TOC entry 3194 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.pricelist_id; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.pricelist_id IS 'Прайслист по которому было тарифицированно плечо';


--
-- TOC entry 3195 (class 0 OID 0)
-- Dependencies: 220
-- Name: COLUMN raw.disconnect_cause; Type: COMMENT; Schema: calls; Owner: postgres
--

COMMENT ON COLUMN calls.raw.disconnect_cause IS 'причина завершения вызова';


--
-- TOC entry 219 (class 1259 OID 16486)
-- Name: raw_id_seq; Type: SEQUENCE; Schema: calls; Owner: postgres
--

CREATE SEQUENCE calls.raw_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE calls.raw_id_seq OWNER TO postgres;

--
-- TOC entry 3196 (class 0 OID 0)
-- Dependencies: 219
-- Name: raw_id_seq; Type: SEQUENCE OWNED BY; Schema: calls; Owner: postgres
--

ALTER SEQUENCE calls.raw_id_seq OWNED BY calls.raw.id;


--
-- TOC entry 2920 (class 2604 OID 16419)
-- Name: trunk id; Type: DEFAULT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth.trunk ALTER COLUMN id SET DEFAULT nextval('auth.trunk_id_seq'::regclass);


--
-- TOC entry 2932 (class 2604 OID 16557)
-- Name: user id; Type: DEFAULT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth."user" ALTER COLUMN id SET DEFAULT nextval('auth.user_id_seq'::regclass);


--
-- TOC entry 2914 (class 2604 OID 16393)
-- Name: clients id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.clients ALTER COLUMN id SET DEFAULT nextval('billing.clients_id_seq'::regclass);


--
-- TOC entry 2931 (class 2604 OID 16534)
-- Name: packet id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet ALTER COLUMN id SET DEFAULT nextval('billing.packet_id_seq'::regclass);


--
-- TOC entry 2918 (class 2604 OID 16407)
-- Name: pricelist id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist ALTER COLUMN id SET DEFAULT nextval('billing.pricelist_id_seq'::regclass);


--
-- TOC entry 2922 (class 2604 OID 16431)
-- Name: pricelist_item id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist_item ALTER COLUMN id SET DEFAULT nextval('billing.pricelist_item_id_seq'::regclass);


--
-- TOC entry 2923 (class 2604 OID 16444)
-- Name: service_number id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_number ALTER COLUMN id SET DEFAULT nextval('billing.service_number_id_seq'::regclass);


--
-- TOC entry 2924 (class 2604 OID 16460)
-- Name: service_trunk id; Type: DEFAULT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk ALTER COLUMN id SET DEFAULT nextval('billing.service_trunk_id_seq'::regclass);


--
-- TOC entry 2927 (class 2604 OID 16480)
-- Name: cdr id; Type: DEFAULT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.cdr ALTER COLUMN id SET DEFAULT nextval('calls.cdr_id_seq'::regclass);


--
-- TOC entry 2928 (class 2604 OID 16491)
-- Name: raw id; Type: DEFAULT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw ALTER COLUMN id SET DEFAULT nextval('calls.raw_id_seq'::regclass);


--
-- TOC entry 3098 (class 0 OID 16416)
-- Dependencies: 210
-- Data for Name: trunk; Type: TABLE DATA; Schema: auth; Owner: postgres
--

COPY auth.trunk (id, trunk_name, auth_by_number) FROM stdin;
1	vpbx1	t
2	op1	f
3	op2	f
4	op3	f
\.


--
-- TOC entry 3112 (class 0 OID 16554)
-- Dependencies: 224
-- Data for Name: user; Type: TABLE DATA; Schema: auth; Owner: postgres
--

COPY auth."user" (id, client_id, login, password, active) FROM stdin;
1	1	client1	123	t
2	2	client2	123	t
3	3	client3	123	t
\.


--
-- TOC entry 3094 (class 0 OID 16390)
-- Dependencies: 206
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
-- TOC entry 3110 (class 0 OID 16531)
-- Dependencies: 222
-- Data for Name: packet; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.packet (id, service_trunk_id, service_number_id, activation_dt, expire_dt, orig, pricelist_id) FROM stdin;
\.


--
-- TOC entry 3096 (class 0 OID 16404)
-- Dependencies: 208
-- Data for Name: pricelist; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.pricelist (id, name, date_from, date_to, round_to_sec) FROM stdin;
1	розница n1	2020-01-01	\N	f
2	Розница n2	2021-01-01	\N	f
3	Операторский1	2020-01-01	\N	t
4	Операторский2	2019-01-01	\N	t
5	Операторский3	2019-06-06	\N	t
\.


--
-- TOC entry 3100 (class 0 OID 16428)
-- Dependencies: 212
-- Data for Name: pricelist_item; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.pricelist_item (id, pricelist_id, ndef, date_from, date_to, price) FROM stdin;
1	1	7495	2020-01-01	\N	1.2000
4	1	7903	2020-01-02	\N	1.5000
\.


--
-- TOC entry 3102 (class 0 OID 16441)
-- Dependencies: 214
-- Data for Name: service_number; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.service_number (id, client_id, did, activation_dt, expire_dt) FROM stdin;
1	1	74951110001	2025-01-01 00:00:00+00	\N
2	1	74951110002	2025-01-01 00:00:00+00	20025-02-02 00:00:00+00
3	2	74952220001	2021-01-01 00:00:00+00	\N
4	3	74963330003	2022-01-01 00:00:00+00	\N
\.


--
-- TOC entry 3104 (class 0 OID 16457)
-- Dependencies: 216
-- Data for Name: service_trunk; Type: TABLE DATA; Schema: billing; Owner: postgres
--

COPY billing.service_trunk (id, client_id, trunk_id, activation_dt, expire_dt, orig_enabled, term_enabled) FROM stdin;
1	2	2	2021-01-01 00:00:00+00	\N	t	t
2	3	3	2020-04-04 00:00:00+00	\N	t	t
3	4	4	2020-02-02 00:00:00+00	\N	f	t
\.


--
-- TOC entry 3106 (class 0 OID 16477)
-- Dependencies: 218
-- Data for Name: cdr; Type: TABLE DATA; Schema: calls; Owner: postgres
--

COPY calls.cdr (id, call_id, src_number, dst_number, setup_time, connect_time, disconnect_time, session_time, disconnect_cause, src_route, dst_route) FROM stdin;
\.


--
-- TOC entry 3108 (class 0 OID 16488)
-- Dependencies: 220
-- Data for Name: raw; Type: TABLE DATA; Schema: calls; Owner: postgres
--

COPY calls.raw (id, orig, peer_id, cdr_id, connect_time, trunk_id, client_id, service_trunk_id, service_number_id, src_number, dst_number, billed_time, rate, cost, pricelist_id, disconnect_cause) FROM stdin;
\.


--
-- TOC entry 3197 (class 0 OID 0)
-- Dependencies: 209
-- Name: trunk_id_seq; Type: SEQUENCE SET; Schema: auth; Owner: postgres
--

SELECT pg_catalog.setval('auth.trunk_id_seq', 4, true);


--
-- TOC entry 3198 (class 0 OID 0)
-- Dependencies: 223
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: auth; Owner: postgres
--

SELECT pg_catalog.setval('auth.user_id_seq', 3, true);


--
-- TOC entry 3199 (class 0 OID 0)
-- Dependencies: 205
-- Name: clients_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.clients_id_seq', 6, true);


--
-- TOC entry 3200 (class 0 OID 0)
-- Dependencies: 221
-- Name: packet_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.packet_id_seq', 1, true);


--
-- TOC entry 3201 (class 0 OID 0)
-- Dependencies: 207
-- Name: pricelist_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.pricelist_id_seq', 5, true);


--
-- TOC entry 3202 (class 0 OID 0)
-- Dependencies: 211
-- Name: pricelist_item_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.pricelist_item_id_seq', 4, true);


--
-- TOC entry 3203 (class 0 OID 0)
-- Dependencies: 213
-- Name: service_number_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.service_number_id_seq', 4, true);


--
-- TOC entry 3204 (class 0 OID 0)
-- Dependencies: 215
-- Name: service_trunk_id_seq; Type: SEQUENCE SET; Schema: billing; Owner: postgres
--

SELECT pg_catalog.setval('billing.service_trunk_id_seq', 3, true);


--
-- TOC entry 3205 (class 0 OID 0)
-- Dependencies: 217
-- Name: cdr_id_seq; Type: SEQUENCE SET; Schema: calls; Owner: postgres
--

SELECT pg_catalog.setval('calls.cdr_id_seq', 1, false);


--
-- TOC entry 3206 (class 0 OID 0)
-- Dependencies: 219
-- Name: raw_id_seq; Type: SEQUENCE SET; Schema: calls; Owner: postgres
--

SELECT pg_catalog.setval('calls.raw_id_seq', 1, false);


--
-- TOC entry 2938 (class 2606 OID 16425)
-- Name: trunk trunk_idx; Type: CONSTRAINT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth.trunk
    ADD CONSTRAINT trunk_idx PRIMARY KEY (id);


--
-- TOC entry 2952 (class 2606 OID 16562)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- TOC entry 2934 (class 2606 OID 16401)
-- Name: clients clients_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);


--
-- TOC entry 2940 (class 2606 OID 16433)
-- Name: pricelist_item defs_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist_item
    ADD CONSTRAINT defs_pkey PRIMARY KEY (ndef, pricelist_id);


--
-- TOC entry 2950 (class 2606 OID 16536)
-- Name: packet packets_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT packets_pkey PRIMARY KEY (id);


--
-- TOC entry 2936 (class 2606 OID 16413)
-- Name: pricelist pricelist_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist
    ADD CONSTRAINT pricelist_pkey PRIMARY KEY (id);


--
-- TOC entry 2942 (class 2606 OID 16449)
-- Name: service_number service_number_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_number
    ADD CONSTRAINT service_number_pkey PRIMARY KEY (id);


--
-- TOC entry 2944 (class 2606 OID 16464)
-- Name: service_trunk service_trunk_pkey; Type: CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk
    ADD CONSTRAINT service_trunk_pkey PRIMARY KEY (id);


--
-- TOC entry 2948 (class 2606 OID 16498)
-- Name: raw calls_raw_pkey; Type: CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT calls_raw_pkey PRIMARY KEY (id);


--
-- TOC entry 2946 (class 2606 OID 16485)
-- Name: cdr cdr_pkey; Type: CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.cdr
    ADD CONSTRAINT cdr_pkey PRIMARY KEY (id);


--
-- TOC entry 2966 (class 2606 OID 16563)
-- Name: user fk_client_id; Type: FK CONSTRAINT; Schema: auth; Owner: postgres
--

ALTER TABLE ONLY auth."user"
    ADD CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- TOC entry 2954 (class 2606 OID 16450)
-- Name: service_number fk_clients_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_number
    ADD CONSTRAINT fk_clients_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- TOC entry 2955 (class 2606 OID 16465)
-- Name: service_trunk fk_clients_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk
    ADD CONSTRAINT fk_clients_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- TOC entry 2953 (class 2606 OID 16434)
-- Name: pricelist_item fk_pricelist_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.pricelist_item
    ADD CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist(id);


--
-- TOC entry 2963 (class 2606 OID 16547)
-- Name: packet fk_pricelist_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist(id);


--
-- TOC entry 2964 (class 2606 OID 16542)
-- Name: packet fk_service_number_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT fk_service_number_id FOREIGN KEY (service_number_id) REFERENCES billing.service_number(id);


--
-- TOC entry 2965 (class 2606 OID 16537)
-- Name: packet fk_service_trunk_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.packet
    ADD CONSTRAINT fk_service_trunk_id FOREIGN KEY (service_trunk_id) REFERENCES billing.service_trunk(id);


--
-- TOC entry 2956 (class 2606 OID 16470)
-- Name: service_trunk fk_trunk_id; Type: FK CONSTRAINT; Schema: billing; Owner: postgres
--

ALTER TABLE ONLY billing.service_trunk
    ADD CONSTRAINT fk_trunk_id FOREIGN KEY (trunk_id) REFERENCES auth.trunk(id);


--
-- TOC entry 2957 (class 2606 OID 16499)
-- Name: raw fk_cdr_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_cdr_id FOREIGN KEY (cdr_id) REFERENCES calls.cdr(id);


--
-- TOC entry 2958 (class 2606 OID 16509)
-- Name: raw fk_client_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES billing.clients(id);


--
-- TOC entry 2959 (class 2606 OID 16524)
-- Name: raw fk_pricelist_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_pricelist_id FOREIGN KEY (pricelist_id) REFERENCES billing.pricelist(id);


--
-- TOC entry 2960 (class 2606 OID 16519)
-- Name: raw fk_service_number_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_service_number_id FOREIGN KEY (service_number_id) REFERENCES billing.service_number(id);


--
-- TOC entry 2961 (class 2606 OID 16514)
-- Name: raw fk_service_trunk_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_service_trunk_id FOREIGN KEY (service_trunk_id) REFERENCES billing.service_trunk(id);


--
-- TOC entry 2962 (class 2606 OID 16504)
-- Name: raw fk_trunk_id; Type: FK CONSTRAINT; Schema: calls; Owner: postgres
--

ALTER TABLE ONLY calls.raw
    ADD CONSTRAINT fk_trunk_id FOREIGN KEY (trunk_id) REFERENCES auth.trunk(id);


--
-- TOC entry 3118 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2025-04-28 17:46:57 UTC

--
-- PostgreSQL database dump complete
--

