create table users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email varchar(100) not null,
    password varchar(100) not null
);