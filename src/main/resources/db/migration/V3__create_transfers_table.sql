CREATE OR REPLACE TABLE transfers (
    id BINARY(16) NOT NULL PRIMARY KEY,
    source_id BINARY(16) NOT NULL,
    destination_id BINARY(16) NOT NULL,
    message VARCHAR(300),
    amount BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(20),
    CONSTRAINT f_transfers_s FOREIGN KEY (source_id) REFERENCES wallets (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT f_transfers_d FOREIGN KEY (destination_id) REFERENCES wallets (id) ON DELETE CASCADE ON UPDATE CASCADE
) CHARACTER SET utf8;
