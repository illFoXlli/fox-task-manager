#!/bin/bash
set -e

export PGPASSWORD="$POSTGRES_ADMIN_PASSWORD"

echo "Waiting for PostgreSQL..."

until pg_isready -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER"; do
  sleep 2
done

echo "PostgreSQL is ready"

echo "Checking database user..."

psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d postgres <<SQL
do
\$\$
begin
    if not exists (
        select from pg_catalog.pg_roles
        where rolname = '${DB_USER}'
    ) then
        create role ${DB_USER} with login password '${DB_PASSWORD}';
    end if;
end
\$\$;
SQL

echo "Checking database..."

DB_EXISTS=$(psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d postgres -tAc "select 1 from pg_database where datname='${DB_NAME}'")

if [ "$DB_EXISTS" != "1" ]; then
  createdb -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -O "$DB_USER" "$DB_NAME"
  echo "Database $DB_NAME created"
else
  echo "Database $DB_NAME already exists"
fi

echo "Granting privileges..."

psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d "$DB_NAME" <<SQL
grant all privileges on database ${DB_NAME} to ${DB_USER};
grant all on schema public to ${DB_USER};
SQL

echo "Database init completed"