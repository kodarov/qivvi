-- liquibase formatted sql

--changeset 1 kodarov_s: transactionType
CREATE TYPE transaction_type AS ENUM (
    'DEPOSIT',
    'WITHDRAW');

--changeset 2 kodarov_s: wallets
CREATE TABLE wallets
(
    id      UUID PRIMARY KEY,
    balance NUMERIC NOT NULL
);

--changeset 3 kodarov_s: operations
CREATE TABLE operations
(
    id             BIGSERIAL PRIMARY KEY,
    operation      transaction_type NOT NULL,
    operation_date TIMESTAMP        NOT NULL DEFAULT CURRENT_DATE,
    amount         NUMERIC          NOT NULL,
    wallets_id     UUID          NOT NULL,
    FOREIGN KEY (wallets_id) REFERENCES wallets (id)
);


