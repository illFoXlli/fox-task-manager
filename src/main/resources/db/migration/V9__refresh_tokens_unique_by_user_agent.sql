drop index if exists ux_refresh_tokens_client;

with ranked_refresh_tokens as (
    select
        id,
        row_number() over (
            partition by
                user_profile_id,
                source,
                coalesce(user_agent, '')
            order by updated_at desc, id desc
        ) as row_number
    from refresh_tokens
)
delete from refresh_tokens
where id in (
    select id
    from ranked_refresh_tokens
    where row_number > 1
);

create unique index ux_refresh_tokens_user_agent
    on refresh_tokens (
        user_profile_id,
        source,
        coalesce(user_agent, '')
    );
