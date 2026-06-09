create index if not exists idx_telegram_auth_sessions_status_expires_at
    on telegram_auth_sessions(status, expires_at);

create index if not exists idx_telegram_auth_sessions_status_updated_at
    on telegram_auth_sessions(status, updated_at);

create index if not exists idx_telegram_auth_sessions_telegram_mode_status
    on telegram_auth_sessions(telegram_id, mode, status)
    where telegram_id is not null;
