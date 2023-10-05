
package com.pedrogonzalezj.banking.domain.transfer;

import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {
    private Transfer transfer;
    private Wallet source;
    private Wallet destination;

    @BeforeEach
    public void setup() {
        source = new Wallet(UUID.randomUUID(), "John's Doe Wallet", UUID.randomUUID());
        source.setMoney(BigInteger.valueOf(100));
        destination = new Wallet(UUID.randomUUID(), "Jane's Doe Wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.TEN);
        transfer = new Transfer(UUID.randomUUID(), source.getId(), destination.getId(), 10L, "test");
    }

    @Test
    public void putInWithdrawnMoney_updatesTransferStatus() {
        final var when = LocalDateTime.of(2023,1,1,0,0,0).toInstant(ZoneOffset.UTC);
        transfer.putInWithdrawnMoney(when);

        assertEquals(Status.WITHDRAWN, transfer.getStatus());
        assertEquals(when, transfer.getUpdatedAt());
        assertNotNull(transfer.getDomainEvents());
        assertEquals(1, transfer.getDomainEvents().size());
        assertThat(transfer.getDomainEvents(), hasItem(allOf(
                instanceOf(NotedCashWithdrawalEvent.class),
                hasProperty("transferId", equalTo(transfer.getId())),
                hasProperty("source", equalTo(transfer.getSource())),
                hasProperty("amount", equalTo(transfer.getAmount()))
        )));
    }

    @Test
    public void finish_updatesTransferStatus() {
        final var when = LocalDateTime.of(2023,1,1,0,0,0).toInstant(ZoneOffset.UTC);
        transfer.finish(when);

        assertEquals(Status.FINISHED, transfer.getStatus());
        assertEquals(when, transfer.getUpdatedAt());
        assertNotNull(transfer.getDomainEvents());
        assertEquals(1, transfer.getDomainEvents().size());
        assertThat(transfer.getDomainEvents(), hasItem(allOf(
                instanceOf(TransferPerformedEvent.class),
                hasProperty("transferId", equalTo(transfer.getId()))
        )));
    }
}
