CREATE OR REPLACE TABLE operations (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(200),
    transfer_id BINARY(16),
    amount BIGINT UNSIGNED NOT NULL,
    wallet_id BINARY(16),
    created_at TIMESTAMP,
    CONSTRAINT f_operations_wallets FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT f_operations_transfers FOREIGN KEY (transfer_id) REFERENCES transfers (id) ON DELETE CASCADE ON UPDATE CASCADE
) CHARACTER SET utf8;
