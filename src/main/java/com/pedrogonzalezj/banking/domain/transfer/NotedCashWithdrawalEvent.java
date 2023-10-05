
package com.pedrogonzalezj.banking.domain.transfer;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class NotedCashWithdrawalEvent extends Event {
    private final UUID transferId;
    private final UUID source;
    private final Long amount;

    public NotedCashWithdrawalEvent(
            UUID transferId,
            UUID source,
            Long amount,
            Instant createdAt) {
        super(createdAt);
        this.transferId = transferId;
        this.source = source;
        this.amount = amount;
    }
}
