SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

CREATE ROLE bill_daemon_local;
ALTER ROLE bill_daemon_local WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION PASSWORD 'md50b11a11686f194f92e10fa9eb342771c';

