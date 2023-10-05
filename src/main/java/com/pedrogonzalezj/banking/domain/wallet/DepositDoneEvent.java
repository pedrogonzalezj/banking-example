
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class DepositDoneEvent extends Event {
    private final UUID walletId;
    private final Long amount;

    public DepositDoneEvent(UUID walletId,
                            Long amount,
                            Instant createdAt) {
        super(createdAt);
        this.walletId = walletId;
        this.amount = amount;
    }
}
