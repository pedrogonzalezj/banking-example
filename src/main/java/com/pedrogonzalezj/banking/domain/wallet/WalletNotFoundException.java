
package com.pedrogonzalezj.banking.domain.wallet;

import lombok.Getter;

import java.util.UUID;

public class WalletNotFoundException extends RuntimeException {
    @Getter
    private final UUID walletId;

    public WalletNotFoundException(final UUID walletId) {
        super("Wallet with id=%s not found".formatted(walletId));
        this.walletId = walletId;
    }
}
