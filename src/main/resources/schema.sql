-- Таблица с постами
create table if not exists posts (
    id bigserial primary key,
    title varchar(256) not null,
    text text not null,
    tags varchar(1024),
    likes_count bigint default 0,
    comments_count bigint default 0
);