
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import com.pedrogonzalezj.banking.domain.transfer.TransferCreatedEvent;
import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import com.pedrogonzalezj.banking.domain.wallet.WalletNotFoundException;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WithdrawTransferMoneyUseCaseTest {
    private WithdrawTransferMoneyUseCase useCase;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransferRepository transferRepository;

    @BeforeEach
    public void setup() {
        useCase = new WithdrawTransferMoneyUseCase(walletRepository, transferRepository);
    }

    @Test
    public void withdrawTransferMoney_withdrawnTransferAmount() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's Transfer";
        final var amount = 10L;
        final var source = new Wallet(sourceId, "John Doe's wallet", UUID.randomUUID());
        source.setMoney(BigInteger.TEN);
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, message);
        final var event = new TransferCreatedEvent(transferId, sourceId, destinationId, amount, message, Instant.now());

        when(walletRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));

        useCase.withdrawTransferMoney(event);

        verify(walletRepository).findById(sourceId);
        verify(transferRepository).findById(transferId);
        verify(walletRepository).save(
                argThat(w ->
                        BigInteger.ZERO.equals(w.getMoney()) &&
                        w.getOperations().size() == 1 &&
                        w.getDomainEvents().size() == 1
                )
        );
    }

    @Test
    public void withdrawTransferMoney_notFoundWallet_throwsException() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's Transfer";
        final var amount = 10L;
        final var source = new Wallet(sourceId, "John Doe's wallet", UUID.randomUUID());
        source.setMoney(BigInteger.TEN);
        final var event = new TransferCreatedEvent(transferId, sourceId, destinationId, amount, message, Instant.now());

        when(walletRepository.findById(sourceId)).thenReturn(Optional.empty());

        final var ex = assertThrows(WalletNotFoundException.class, () -> useCase.withdrawTransferMoney(event));

        verify(walletRepository).findById(sourceId);
        verify(transferRepository, never()).findById(any());
        verify(walletRepository, never()).save(any());
        assertEquals("Wallet with id=%s not found".formatted(sourceId), ex.getMessage());
    }

    @Test
    public void withdrawTransferMoney_notFoundTransfer_throwsException() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's Transfer";
        final var amount = 10L;
        final var source = new Wallet(sourceId, "John Doe's wallet", UUID.randomUUID());
        source.setMoney(BigInteger.TEN);
        final var event = new TransferCreatedEvent(transferId, sourceId, destinationId, amount, message, Instant.now());

        when(walletRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(transferRepository.findById(transferId)).thenReturn(Optional.empty());

        final var ex = assertThrows(TransferNotFoundException.class, () -> useCase.withdrawTransferMoney(event));

        verify(walletRepository).findById(sourceId);
        verify(transferRepository).findById(transferId);
        verify(walletRepository, never()).save(any());
        assertEquals("Transfer with id=%s not found".formatted(transferId), ex.getMessage());
    }
}
