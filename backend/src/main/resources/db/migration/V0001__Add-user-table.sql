CREATE TABLE "users"
(
    id              UUID         NOT NULL PRIMARY KEY,
    name            VARCHAR(120),
    email           VARCHAR(30)  NOT NULL,
    password        VARCHAR(300) NOT NULL,
    emailIsVerified BOOLEAN DEFAULT FALSE,
    CONSTRAINT unique_email UNIQUE (email)
);
