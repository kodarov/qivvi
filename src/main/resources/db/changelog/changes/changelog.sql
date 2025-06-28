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

--changeset 4 kodarov_s: add_audit_fields_to_operations
ALTER TABLE operations 
ADD COLUMN balance_before NUMERIC(19,2),
ADD COLUMN balance_after NUMERIC(19,2),
ADD COLUMN operation_id VARCHAR(36) UNIQUE;

--changeset 5 kodarov_s: add_indexes_to_operations
CREATE INDEX idx_operations_wallet_id ON operations(wallets_id);
CREATE INDEX idx_operations_date ON operations(operation_date);
CREATE INDEX idx_operations_type ON operations(operation);


