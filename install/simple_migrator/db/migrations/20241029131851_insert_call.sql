CREATE INDEX uvrcdr_call_id_call_type_idx on uvrstorage.uvrcdr (call_id, call_type);

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
  rec_exists boolean;
begin
	suffix := to_char(new.dt_connect, 'YYYYMM');
	relname := 'uvrstorage.uvrcdr_' || suffix;
	EXECUTE 'SELECT to_regclass('|| quote_literal(relname) ||');' INTO rel_exists;
	
	IF rel_exists IS NULL OR rel_exists = ''
	THEN
		EXECUTE 'select uvrstorage.create_uvrcdr_partition(' || quote_literal(new.dt_connect) || '::timestamptz)';
	END IF;

  EXECUTE 'SELECT EXISTS (SELECT dt_insert FROM ' || relname || ' WHERE call_id=' || quote_literal(new.call_id) || 'and call_type='|| quote_literal(new.call_type) || ')' INTO rec_exists;
  IF NOT rec_exists
  THEN
    EXECUTE format('INSERT INTO ' || relname || ' SELECT ($1).*') USING NEW;
  END IF;
	return null;        
end;
$BODY$;
