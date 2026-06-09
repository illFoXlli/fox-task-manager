create table telegram_auth_sessions (
    id bigserial primary key,
    token_hash varchar(64) not null unique,
    mode varchar(20) not null,
    status varchar(30) not null,
    telegram_id bigint,
    telegram_username varchar(100),
    telegram_first_name varchar(100),
    telegram_last_name varchar(100),
    telegram_photo_url text,
    user_profile_id bigint references user_profiles(id),
    expires_at timestamp not null,
    confirmed_at timestamp,
    created_at timestamp not null,
    updated_at timestamp not null
);

create index idx_telegram_auth_sessions_status
    on telegram_auth_sessions(status);

create index idx_telegram_auth_sessions_expires_at
    on telegram_auth_sessions(expires_at);
