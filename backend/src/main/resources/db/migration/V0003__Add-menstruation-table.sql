CREATE TABLE "menstruation"
(
    user_id    UUID      NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date   TIMESTAMP NOT NULL,
    CONSTRAINT unique_menstruation UNIQUE (user_id, start_date, end_date)
);
