
package com.pedrogonzalezj.banking.domain.transfer;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public class TransferPerformedEvent extends Event {
    private final UUID transferId;

    public TransferPerformedEvent(UUID transferId, Instant createdAt) {
        super(createdAt);
        this.transferId = transferId;
    }
}
