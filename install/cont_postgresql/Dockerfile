FROM postgres:12

RUN   apt update; \
      apt-get install -y postgresql-12-wal2json

RUN   sed -i -e"s/^#wal_level =.*$/wal_level = logical/" /usr/share/postgresql/postgresql.conf.sample; \
      sed -i -e"s/^#max_replication_slots =.*$/max_replication_slots = 10/" /usr/share/postgresql/postgresql.conf.sample; \
      sed -i -e"s/^#max_wal_senders =.*$/max_wal_senders = 10/" /usr/share/postgresql/postgresql.conf.sample

EXPOSE 5032
