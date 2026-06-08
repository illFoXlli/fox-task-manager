create table refresh_tokens (
                                id bigserial primary key,
                                token_hash varchar(255) not null unique,
                                user_profile_id bigint not null,
                                source varchar(30) not null default 'WEB',
                                user_agent text,
                                ip_address varchar(100),
                                issued_at timestamp not null default current_timestamp,
                                expires_at timestamp not null,
                                revoked_at timestamp,
                                created_at timestamp not null default current_timestamp,
                                updated_at timestamp not null default current_timestamp,
                                constraint fk_refresh_tokens_user_profile
                                    foreign key (user_profile_id)
                                        references user_profiles(id)
                                        on delete cascade
);

create index idx_refresh_tokens_token_hash on refresh_tokens(token_hash);

create index idx_refresh_tokens_user_profile_id
    on refresh_tokens(user_profile_id);

create index idx_refresh_tokens_expires_at on refresh_tokens(expires_at);