CREATE TABLE "tokens"
(
    user_id    UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    expires_at TIMESTAMP,
    CONSTRAINT unique_token UNIQUE (user_id, created_at)
);
