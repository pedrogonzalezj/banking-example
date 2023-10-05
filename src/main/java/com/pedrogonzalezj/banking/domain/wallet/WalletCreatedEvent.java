
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class WalletCreatedEvent extends Event {
    private final UUID walletId;

    public WalletCreatedEvent(UUID walletId,
                            Instant createdAt) {
        super(createdAt);
        this.walletId = walletId;
    }
}
