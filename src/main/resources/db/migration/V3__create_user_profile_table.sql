create table user_profiles (
                               id bigserial primary key,
                               login varchar(100) not null unique,
                               email varchar(255),
                               password_hash varchar(255),
                               display_name varchar(100),
                               language_code varchar(10) not null default 'uk',
                               role varchar(30) not null default 'USER',
                               auth_provider varchar(30) not null default 'WEB',
                               telegram_id bigint unique,
                               telegram_username varchar(100),
                               telegram_first_name varchar(100),
                               telegram_last_name varchar(100),
                               telegram_photo_url text,
                               enabled boolean not null default true,
                               account_locked boolean not null default false,
                               online boolean not null default false,
                               last_seen_at timestamp,
                               first_login_at timestamp,
                               last_login_at timestamp,
                               web_last_login_at timestamp,
                               telegram_last_login_at timestamp,
                               created_at timestamp not null default current_timestamp,
                               updated_at timestamp not null default current_timestamp
);

create index idx_user_profiles_login on user_profiles(login);

create index idx_user_profiles_telegram_id on user_profiles(telegram_id);

create index idx_user_profiles_role on user_profiles(role);