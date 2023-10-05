
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Getter
public class CashWithdrawnEvent extends Event {
    private final UUID source;
    private final BigInteger amount;

    public CashWithdrawnEvent(UUID source,
                              BigInteger amount,
                              Instant createdAt) {
        super(createdAt);
        this.source = source;
        this.amount = amount;
    }
}
