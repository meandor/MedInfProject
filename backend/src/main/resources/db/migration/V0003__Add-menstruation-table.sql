CREATE TABLE "menstruation"
(
    user_id    UUID      NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    CONSTRAINT unique_menstruation UNIQUE (user_id, start_date, end_date)
);
