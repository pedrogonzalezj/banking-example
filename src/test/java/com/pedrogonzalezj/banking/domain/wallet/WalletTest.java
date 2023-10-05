
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import com.pedrogonzalezj.banking.domain.transfer.TransferCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.UUID;

import static com.pedrogonzalezj.banking.domain.wallet.Operation.DEPOSIT_OPERATION;
import static com.pedrogonzalezj.banking.domain.wallet.Operation.WITHDRAWAL_OPERATION;
import static com.pedrogonzalezj.banking.domain.wallet.Wallet.DEPOSIT_MESSAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

    private Wallet wallet;


    @BeforeEach
    public void setup() {
        wallet = new Wallet(UUID.randomUUID(), "John's Doe wallet", UUID.randomUUID());
        wallet.setMoney(BigInteger.TEN);
    }


    @Test
    public void wireTransfer_emptyDestination_throwsException() {
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.wireTransfer(null, "test", 10L)
        );
        assertEquals("Destination wallet is required", ex.getMessage());
    }

    @Test
    public void wireTransfer_negativeAmount_throwsException() {
        final var destination = new Wallet(UUID.randomUUID(), "Jane's Doe Wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.TEN);
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.wireTransfer(destination, "test", -1L)
        );
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    public void wireTransfer_ZeroAmount_ThrowsException() {
        final var destination = new Wallet(UUID.randomUUID(), "Jane's Doe Wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.TEN);
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.wireTransfer(destination, "test", 0L)
        );
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    public void wireTransfer_transferAmountGreaterThanSourceWalletAmount_throwsException() {
        final var destination = new Wallet(UUID.randomUUID(), "Jane's Doe Wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.TEN);
        wallet.setMoney(BigInteger.ONE);
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.wireTransfer(destination, "test", 10L)
        );
        assertEquals("Can not withdraw more money than current savings", ex.getMessage());
    }

    @Test
    public void wireTransfer_validArguments_createsATransfer() {
        final var transferMessage = "test";
        final var transferAmount = 10L;
        final var destination = new Wallet(UUID.randomUUID(), "Jane's Doe Wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.TEN);
        final var results = wallet.wireTransfer(destination, transferMessage, transferAmount);

        assertNotNull(results);
        assertNotNull(results.getId());
        assertEquals(wallet.getId(), results.getSource());
        assertEquals(destination.getId(), results.getDestination());
        assertEquals(transferAmount, results.getAmount());
        assertEquals(transferMessage, results.getMessage());
        assertNotNull(results.getDomainEvents());
        assertEquals(1, results.getDomainEvents().size());
        assertThat(results.getDomainEvents(), hasItem(allOf(
                instanceOf(TransferCreatedEvent.class),
                hasProperty("transferId", equalTo(results.getId())),
                hasProperty("source", equalTo(results.getSource())),
                hasProperty("destination", equalTo(results.getDestination())),
                hasProperty("message", equalTo(results.getMessage())),
                hasProperty("amount", equalTo(results.getAmount()))
        )));
    }

    @Test
    public void makeDeposit_emptyTransfer_throwsException() {
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.makeDeposit((Transfer) null)
        );
        assertEquals("deposit transfer is required", ex.getMessage());
    }

    @Test
    public void makeDeposit_wrongTransferDestination_throwsException() {
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), transferAmount, "random");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.makeDeposit(transfer)
        );
        assertEquals("transfer target: %s is not the same as current wallet: %s".formatted(transfer.getDestination(), wallet.getId()), ex.getMessage());
    }

    @Test
    public void makeDeposit_putsTransferAmountIntoWallet() {
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), wallet.getId(), transferAmount, "random");
        wallet.setMoney(BigInteger.ZERO);
        wallet.makeDeposit(transfer);
        assertEquals(BigInteger.valueOf(transferAmount), wallet.getMoney());
    }

    @Test
    public void makeDeposit_fromTransfer_createsNewWalletOperation() {
        final var transferMessage = "random message";
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), wallet.getId(), transferAmount, transferMessage);
        wallet.makeDeposit(transfer);
        assertNotNull(wallet.getOperations());
        assertEquals(1, wallet.getOperations().size());
        final var operation = wallet.getOperations().get(0);
        assertEquals(DEPOSIT_OPERATION + transferMessage, operation.getMessage());
        assertEquals(transferAmount, operation.getAmount());
        assertEquals(transfer.getId(), operation.getTransferId());
        assertEquals(wallet.getId(), operation.getWalletId());
    }

    @Test
    public void makeDeposit_fromAmount_createsNewWalletOperation() {
        final var transferAmount = 10L;
        wallet.makeDeposit(transferAmount);
        assertNotNull(wallet.getOperations());
        assertEquals(1, wallet.getOperations().size());
        final var operation = wallet.getOperations().get(0);
        assertEquals(DEPOSIT_OPERATION + DEPOSIT_MESSAGE, operation.getMessage());
        assertNull(operation.getTransferId());
        assertEquals(transferAmount, operation.getAmount());
        assertEquals(wallet.getId(), operation.getWalletId());
    }

    @Test
    public void makeDeposit_fromTransfer_createsNewEvent() {
        final var transferMessage = "random message";
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), wallet.getId(), transferAmount, transferMessage);
        wallet.makeDeposit(transfer);
        assertNotNull(wallet.getDomainEvents());
        assertEquals(1, wallet.getDomainEvents().size());
        assertThat(wallet.getDomainEvents(), hasItem(allOf(
                instanceOf(MoneyTransferDepositedEvent.class),
                hasProperty("transferId", equalTo(transfer.getId())),
                hasProperty("destination", equalTo(transfer.getDestination())),
                hasProperty("amount", equalTo(transfer.getAmount()))
        )));
    }

    @Test
    public void makeDeposit_fromAmount_createsNewEvent() {
        final var transferAmount = 10L;
        wallet.makeDeposit(transferAmount);
        assertNotNull(wallet.getOperations());
        assertEquals(1, wallet.getOperations().size());
        assertThat(wallet.getDomainEvents(), hasItem(allOf(
                instanceOf(DepositDoneEvent.class),
                hasProperty("walletId", equalTo(wallet.getId())),
                hasProperty("amount", equalTo(transferAmount))
        )));
    }

    @Test
    public void withdrawMoney_emptyTransfer_throwsException() {
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.withdrawMoney(null)
        );
        assertEquals("withdrawal transfer is required", ex.getMessage());
    }

    @Test
    public void withdrawMoney_wrongTransferSource_throwsException() {
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), transferAmount, "random");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.withdrawMoney(transfer)
        );
        assertEquals("withdraw source: %s is not the same as current wallet: %s".formatted(transfer.getSource(), wallet.getId()), ex.getMessage());
    }

    @Test
    public void withdrawMoney_emptyAmount_throwsException() {
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), null, "random");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.withdrawMoney(transfer)
        );
        assertEquals("Amount must be provided", ex.getMessage());
    }

    @Test
    public void withdrawMoney_zeroAmount_throwsException() {
        final var transferAmount = 0L;
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), transferAmount, "random");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.withdrawMoney(transfer)
        );
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    public void withdrawMoney_negativeAmount_throwsException() {
        final var transferAmount = -10L;
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), transferAmount, "random");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.withdrawMoney(transfer)
        );
        assertEquals("Amount must be greater than zero", ex.getMessage());
    }

    @Test
    public void withdrawMoney_anAmountGreaterThanWalletAmount_throwsException() {
        wallet.setMoney(BigInteger.TEN);
        final var transferAmount = 11L;
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), transferAmount, "random");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> wallet.withdrawMoney(transfer)
        );
        assertEquals("Can not withdraw more money than current savings", ex.getMessage());
    }

    @Test
    public void withdrawMoney_withdrawsTransferAmountFromWallet() {
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), transferAmount, "random");
        wallet.setMoney(BigInteger.TEN);
        wallet.withdrawMoney(transfer);
        assertEquals(BigInteger.ZERO, wallet.getMoney());
    }

    @Test
    public void withdrawMoney_createsNewWalletOperation() {
        final var transferMessage = "random transfer message";
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), transferAmount, transferMessage);
        wallet.setMoney(BigInteger.TEN);
        wallet.withdrawMoney(transfer);
        assertNotNull(wallet.getOperations());
        assertEquals(1, wallet.getOperations().size());
        final var operation = wallet.getOperations().get(0);
        assertEquals(WITHDRAWAL_OPERATION + transferMessage, operation.getMessage());
        assertEquals(transferAmount, operation.getAmount());
        assertEquals(transfer.getId(), operation.getTransferId());
        assertEquals(wallet.getId(), operation.getWalletId());
    }

    @Test
    public void withdrawMoney_createsNewEvent() {
        final var transferMessage = "random transfer message";
        final var transferAmount = 10L;
        final var transfer = new Transfer(UUID.randomUUID(), wallet.getId(), UUID.randomUUID(), transferAmount, transferMessage);
        wallet.setMoney(BigInteger.TEN);
        wallet.withdrawMoney(transfer);
        assertNotNull(wallet.getDomainEvents());
        assertEquals(1, wallet.getDomainEvents().size());
        assertThat(wallet.getDomainEvents(), hasItem(allOf(
                instanceOf(WithdrawalTransferDoneEvent.class),
                hasProperty("transferId", equalTo(transfer.getId())),
                hasProperty("source", equalTo(transfer.getSource())),
                hasProperty("amount", equalTo(transferAmount))
        )));
    }
}
