
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class MoneyTransferDepositedEvent extends Event {
    private final UUID transferId;
    private final UUID destination;
    private final Long amount;

    public MoneyTransferDepositedEvent(UUID transferId,
                                       UUID destination,
                                       Long amount,
                                       Instant createdAt) {
        super(createdAt);
        this.transferId = transferId;
        this.destination = destination;
        this.amount = amount;
    }
}
