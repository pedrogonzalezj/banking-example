
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

import static com.pedrogonzalezj.banking.domain.wallet.Operation.DEPOSIT_OPERATION;
import static com.pedrogonzalezj.banking.domain.wallet.Operation.WITHDRAWAL_OPERATION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperationTest {

    @Test
    public void withdrawalOperation_createsNewOperation() {
        final var when = Instant.now();
        final var transferMessage = "random transfer message";
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), transferAmount, transferMessage);
        final var operation = Operation.withdrawalOperation(transfer, when);
        assertEquals(transfer.getSource(), operation.getWalletId());
        assertEquals(transfer.getAmount(), operation.getAmount());
        assertEquals(transfer.getId(), operation.getTransferId());
        assertEquals(when, operation.getCreatedAt());
        assertEquals(WITHDRAWAL_OPERATION + transfer.getMessage(), operation.getMessage());
    }

    @Test
    public void depositOperation_createsNewOperation() {
        final var when = Instant.now();
        final var transferMessage = "random transfer message";
        final var transferAmount = 10L;
        final var walletId = UUID.randomUUID();
        final var transferId = UUID.randomUUID();
        final var operation = Operation.depositOperation(walletId, transferMessage, transferAmount, transferId, when);
        assertEquals(walletId, operation.getWalletId());
        assertEquals(transferAmount, operation.getAmount());
        assertEquals(transferId, operation.getTransferId());
        assertEquals(when, operation.getCreatedAt());
        assertEquals(DEPOSIT_OPERATION + transferMessage, operation.getMessage());
    }
}
