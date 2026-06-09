alter table refresh_tokens
    add column last_used_at timestamp;

create index idx_refresh_tokens_last_used_at on refresh_tokens(last_used_at);
