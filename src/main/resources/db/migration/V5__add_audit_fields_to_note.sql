alter table note
    add column created_at timestamp not null default current_timestamp;

alter table note
    add column updated_at timestamp not null default current_timestamp;