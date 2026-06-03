create table note (
                      id bigserial primary key,
                      title varchar(255) not null,
                      content text not null
);