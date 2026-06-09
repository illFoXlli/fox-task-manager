alter table refresh_tokens
    add column device_id varchar(64);

update refresh_tokens
set device_id = 'legacy-' || id
where device_id is null;

alter table refresh_tokens
    alter column device_id set not null;

drop index if exists ux_refresh_tokens_user_agent;

create unique index ux_refresh_tokens_device
    on refresh_tokens (
        user_profile_id,
        source,
        device_id
    );

alter table user_profiles
    add column last_device_id varchar(64),
    add column last_ip_address varchar(100);
