
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.Status;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WireTransferUseCaseTest {
    private WireTransferUseCase useCase;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransferRepository transferRepository;

    @BeforeEach
    public void setup() {
        useCase = new WireTransferUseCase(walletRepository, transferRepository);
    }

    @Test
    public void wireTransfer_createsAndStoresNewTransfer() {
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's Transfer";
        final var amount = 10L;
        final var source = new Wallet(sourceId, "John Doe's wallet", UUID.randomUUID());
        source.setMoney(BigInteger.TEN);
        final var destination = new Wallet(destinationId, "Jane Doe's wallet", UUID.randomUUID());

        when(walletRepository.findById(sourceId)).thenReturn(Optional.of(source));
        when(walletRepository.findById(destinationId)).thenReturn(Optional.of(destination));

        final var transferId = useCase.wireTransfer(sourceId, destinationId, message, amount);

        verify(walletRepository).findById(sourceId);
        verify(walletRepository).findById(destinationId);
        verify(transferRepository).save(
                argThat(t -> {
                    return Status.CREATED == t.getStatus() &&
                            sourceId.equals(t.getSource()) &&
                            destinationId.equals(t.getDestination()) &&
                            amount == t.getAmount() &&
                            t.getDomainEvents().size() == 1;
                })
        );
        assertNotNull(transferId);
    }

    @Test
    public void wireTransfer_notFoundSource_throwsException() {
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's Transfer";
        final var amount = 10L;

        when(walletRepository.findById(sourceId)).thenReturn(Optional.empty());

        final var ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.wireTransfer(sourceId, destinationId, message, amount)
        );

        verify(walletRepository).findById(sourceId);
        verify(walletRepository, never()).findById(destinationId);
        verify(transferRepository, never()).save(any());
        assertEquals("source wallet is required.", ex.getMessage());
    }
}
