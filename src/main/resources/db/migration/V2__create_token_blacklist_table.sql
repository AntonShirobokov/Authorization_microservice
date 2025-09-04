CREATE TABLE token_blacklist (
    id BIGSERIAL PRIMARY KEY,
    jti UUID NOT NULL UNIQUE,
    user_id UUID references users(user_id) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);
