
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.Status;
import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.MoneyTransferDepositedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinishTransferUseCaseTest {
    private FinishTransferUseCase useCase;
    @Mock
    private TransferRepository transferRepository;

    @BeforeEach
    public void setup() {
        useCase = new FinishTransferUseCase(transferRepository);
    }

    @Test
    public void finishTransfer_updatesTransferStatusToFinished() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var event = new MoneyTransferDepositedEvent(transferId, destinationId, amount, Instant.now());
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, "John Doe's Transfer");

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));

        useCase.finishTransfer(event);

        verify(transferRepository).findById(transferId);
        verify(transferRepository).save(
                argThat(t -> Status.FINISHED == t.getStatus() && t.getDomainEvents().size() == 1)
        );
    }

    @Test
    public void finishTransfer_notFoundTransfer_throwsException() {
        final var transferId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var event = new MoneyTransferDepositedEvent(transferId, destinationId, amount, Instant.now());

        when(transferRepository.findById(transferId)).thenReturn(Optional.empty());

        final var ex = assertThrows(TransferNotFoundException.class, () -> useCase.finishTransfer(event));

        verify(transferRepository).findById(transferId);
        verify(transferRepository, never()).save(any());
        assertEquals("Transfer with id=%s not found".formatted(transferId), ex.getMessage());
    }
}
