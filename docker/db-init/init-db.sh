#!/bin/bash
set -e

export PGPASSWORD="$POSTGRES_ADMIN_PASSWORD"

echo "Waiting for PostgreSQL..."

until pg_isready -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d postgres; do
  sleep 2
done

echo "PostgreSQL is ready"

create_user_if_not_exists() {
  local user_name="$1"
  local user_password="$2"

  echo "Checking user $user_name..."

  psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d postgres <<SQL
do
\$\$
begin
    if not exists (
        select from pg_catalog.pg_roles
        where rolname = '${user_name}'
    ) then
        create role ${user_name} with login password '${user_password}';
    end if;
end
\$\$;
SQL
}

create_database_if_not_exists() {
  local database_name="$1"
  local owner_name="$2"

  echo "Checking database $database_name..."

  DB_EXISTS=$(psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d postgres -tAc "select 1 from pg_database where datname='${database_name}'")

  if [ "$DB_EXISTS" != "1" ]; then
    createdb -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -O "$owner_name" "$database_name"
    echo "Database $database_name created"
  else
    echo "Database $database_name already exists"
  fi

  psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_ADMIN_USER" -d "$database_name" <<SQL
grant all privileges on database ${database_name} to ${owner_name};
grant all on schema public to ${owner_name};
SQL
}

create_user_if_not_exists "$DB_USER" "$DB_PASSWORD"
create_database_if_not_exists "$DB_NAME" "$DB_USER"

create_user_if_not_exists "$DB_TEST_USER" "$DB_TEST_PASSWORD"
create_database_if_not_exists "$DB_TEST_NAME" "$DB_TEST_USER"

echo "Database init completed"