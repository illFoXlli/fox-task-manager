update note
set created_at = current_timestamp
where created_at is null;

update note
set updated_at = created_at
where updated_at is null;

alter table note
    alter column created_at set not null;

alter table note
    alter column updated_at set not null;