alter table note
    add column user_profile_id bigint;

update note
set user_profile_id = (
    select id
    from user_profiles
    where role = 'ADMIN'
    order by id
    limit 1
    )
where user_profile_id is null;

alter table note
    alter column user_profile_id set not null;

alter table note
    add constraint fk_note_user_profile
        foreign key (user_profile_id)
            references user_profiles(id)
            on delete cascade;

create index idx_note_user_profile_id on note(user_profile_id);