
package com.pedrogonzalezj.banking.domain.transfer;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class TransferCreatedEvent extends Event {
    private final UUID transferId;
    private final UUID source;
    private final UUID destination;
    private final String message;
    private final Long amount;

    public TransferCreatedEvent(
            UUID transferId,
            UUID source,
            UUID destination,
            Long amount,
            String message,
            Instant createdAt) {
        super(createdAt);
        this.transferId = transferId;
        this.source = source;
        this.destination = destination;
        this.message = message;
        this.amount = amount;
    }
}
