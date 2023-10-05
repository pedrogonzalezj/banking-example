
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;


import java.time.Instant;
import java.util.UUID;

@Getter
public class WithdrawalTransferDoneEvent extends Event {
    private final UUID transferId;
    private final UUID source;
    private final Long amount;

    public WithdrawalTransferDoneEvent(UUID transferId,
                              UUID source,
                              Long amount,
                              Instant createdAt) {
        super(createdAt);
        this.transferId = transferId;
        this.source = source;
        this.amount = amount;
    }
}
