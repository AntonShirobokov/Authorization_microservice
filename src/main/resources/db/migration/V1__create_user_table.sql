create table users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    middle_name varchar(50),

    email varchar(100) NOT NULL UNIQUE,
    password_hash varchar(100) NOT NULL,
    created_at timestamp NOT NULL
);

create table roles(
    role_id serial PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE
);

create table users_roles(
    user_id UUID references users(user_id) NOT NULL,
    role_id int references roles(role_id) NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

insert into roles (name) values ('ROLE_USER');

