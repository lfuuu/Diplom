--DROP SCHEMA IF EXISTS uvrstorage cascade;

CREATE SCHEMA IF NOT EXISTS uvrstorage;

CREATE TABLE IF NOT EXISTS uvrstorage.uvrcdr (
  --id bigint PRIMARY KEY NOT NULL,

  dt_insert timestamptz DEFAULT now(),
  dt_update timestamptz,
  dt_connect timestamptz,
  dt_account timestamptz,
	
  num_a text NOT NULL,
  num_b text NOT NULL,
  num_c text,
  num_d text,

  id_src integer,
  id_dst integer,
  session_id bigint,
  id_rel integer,
  vrf_rsp integer,
  release_code integer,
  duration integer,
  id_uvr_t integer,
  call_id text,
  call_type integer
);

CREATE INDEX IF NOT EXISTS uvrcdr_dt_connect_idx
    ON uvrstorage.uvrcdr USING btree
    (dt_connect ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE INDEX IF NOT EXISTS uvrcdr_numa_numb_idx
    ON uvrstorage.uvrcdr USING btree
    (num_a, num_b)
    TABLESPACE pg_default;

CREATE INDEX IF NOT EXISTS uvrcdr_call_id_idx
    ON uvrstorage.uvrcdr USING btree
    (call_id)
    TABLESPACE pg_default;

CREATE OR REPLACE FUNCTION uvrstorage.create_uvrcdr_partition(
	dt_connect timestamptz)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE SECURITY DEFINER PARALLEL UNSAFE
AS $BODY$
declare
        relname varchar;
        rel_exists text;
        suffix varchar;
        this_mon timestamp;
        next_mon timestamp;
BEGIN
        suffix := to_char(dt_connect, 'YYYYMM');
        relname := 'uvrstorage.uvrcdr_' || suffix;
        raise notice '%', relname;
        EXECUTE 'SELECT to_regclass('|| quote_literal(relname) ||');' INTO rel_exists;

        IF rel_exists IS NULL OR rel_exists = ''
        THEN
                EXECUTE 'select date_trunc(''month'', TIMESTAMP ' || quote_literal(dt_connect) || ' );' INTO this_mon;
                EXECUTE 'select date_trunc(''month'', TIMESTAMP ' || quote_literal(dt_connect) || ' + INTERVAL ''1 MON'');' INTO next_mon;

                EXECUTE 'CREATE TABLE ' || relname || ' (LIKE uvrstorage.uvrcdr INCLUDING ALL) INHERITS (uvrstorage.uvrcdr) WITH (OIDS=FALSE)';
                EXECUTE 'ALTER TABLE ' || relname || ' ADD CONSTRAINT uvrcdr_' || suffix || '_dt_connect_check CHECK (' || 
                        'dt_connect >= ' || quote_literal(this_mon) || '::timestamptz AND ' || 
                        'dt_connect < ' || quote_literal(next_mon) || '::timestamptz)';

                -- EXECUTE 'ALTER TABLE ' || relname || ' OWNER TO postgres';
                -- EXECUTE 'GRANT ALL ON TABLE ' || relname || ' TO postgres';
        END IF;
        return true;
END;
$BODY$;

CREATE OR REPLACE FUNCTION uvrstorage.tr_partitioning()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
declare
	relname varchar;
	rel_exists text;
	suffix varchar;
	this_mon timestamp;
	next_mon timestamp;
begin
	suffix := to_char(new.dt_connect, 'YYYYMM');
	relname := 'uvrstorage.uvrcdr_' || suffix;
	EXECUTE 'SELECT to_regclass('|| quote_literal(relname) ||');' INTO rel_exists;
	
	IF rel_exists IS NULL OR rel_exists = ''
	THEN
		EXECUTE 'select uvrstorage.create_uvrcdr_partition(' || quote_literal(new.dt_connect) || '::timestamptz)';
	END IF;

        EXECUTE format('INSERT INTO ' || relname || ' SELECT ($1).*') USING NEW;
	return null;        
end;
$BODY$;

CREATE TRIGGER partitioning
    BEFORE INSERT
    ON uvrstorage.uvrcdr
    FOR EACH ROW
    EXECUTE FUNCTION uvrstorage.tr_partitioning();

-- INSERT INTO uvrstorage.uvrcdr(
-- 	dt_insert, dt_connect, dt_account, num_a, num_b, num_c, num_d, id_src, id_dst, session_id, id_rel, vrf_rsp, release_code, duration, id_uvr_t, call_id, call_type)
-- 	VALUES (now(), now(), now(), 'numa', 'numb', 'numc', 'numd',
-- 	1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
