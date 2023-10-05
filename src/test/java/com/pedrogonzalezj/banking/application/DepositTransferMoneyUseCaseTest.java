
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.NotedCashWithdrawalEvent;
import com.pedrogonzalezj.banking.domain.transfer.Transfer;
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
public class DepositTransferMoneyUseCaseTest {
    private DepositTransferMoneyUseCase useCase;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransferRepository transferRepository;

    @BeforeEach
    public void setup() {
        useCase = new DepositTransferMoneyUseCase(walletRepository, transferRepository);
    }

    @Test
    public void depositTransfer_increasesDestinationWalletAmountInTransferAmount() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var event = new NotedCashWithdrawalEvent(transferId, sourceId, amount, Instant.now());
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, "John Doe's Transfer");
        final var destination = new Wallet(destinationId, "Jane Doe's wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.ZERO);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(walletRepository.findById(destinationId)).thenReturn(Optional.of(destination));

        useCase.depositTransfer(event);

        verify(transferRepository).findById(transferId);
        verify(walletRepository).findById(destinationId);
        verify(walletRepository).save(
                argThat(w ->
                        BigInteger.valueOf(amount).equals(w.getMoney()) &&
                        w.getOperations().size() == 1 &&
                        w.getDomainEvents().size() == 1
                )
        );
    }

    @Test
    public void depositTransfer_notFoundTransfer_throwsException() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var event = new NotedCashWithdrawalEvent(transferId, sourceId, amount, Instant.now());
        final var destination = new Wallet(destinationId, "Jane Doe's wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.ZERO);

        when(transferRepository.findById(transferId)).thenReturn(Optional.empty());

        final var ex = assertThrows(
                TransferNotFoundException.class,
                () -> useCase.depositTransfer(event)
        );

        verify(transferRepository).findById(transferId);
        verify(walletRepository, never()).save(any());
        assertEquals("Transfer with id=%s not found".formatted(transferId), ex.getMessage());
    }

    @Test
    public void depositTransfer_notFoundDestinationWallet_throwsException() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var event = new NotedCashWithdrawalEvent(transferId, sourceId, amount, Instant.now());
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, "John Doe's Transfer");
        final var destination = new Wallet(destinationId, "Jane Doe's wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.ZERO);

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));
        when(walletRepository.findById(destinationId)).thenReturn(Optional.empty());

        final var ex = assertThrows(
                WalletNotFoundException.class,
                () -> useCase.depositTransfer(event)
        );

        verify(transferRepository).findById(transferId);
        verify(walletRepository).findById(destinationId);
        verify(walletRepository, never()).save(any());
        assertEquals("Wallet with id=%s not found".formatted(destinationId), ex.getMessage());
    }
}
