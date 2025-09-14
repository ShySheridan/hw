-- Пользователи
create table if not exists app_user (
                                        id            bigserial primary key,
                                        login         text unique not null,
                                        password_hash text        not null
);

-- Коллекция: храним бинарь сериализованного объекта, и отдельные служебные поля
create table if not exists labwork (
                                       id          bigserial primary key,
                                       owner_id    bigint     not null references app_user(id) on delete cascade,
    owner_login text       not null,
    payload     bytea      not null,
    created_at  timestamptz default now()
    );

-- Индексы для частых проверок прав
create index if not exists idx_labwork_owner on labwork(owner_id);
