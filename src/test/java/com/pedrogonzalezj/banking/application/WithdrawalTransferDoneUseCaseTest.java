
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.Status;
import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.WithdrawalTransferDoneEvent;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WithdrawalTransferDoneUseCaseTest {
    private WithdrawalTransferDoneUseCase useCase;
    @Mock
    private TransferRepository transferRepository;

    @BeforeEach
    public void setup() {
        useCase = new WithdrawalTransferDoneUseCase(transferRepository);
    }

    @Test
    public void updateTransferStatus_updatesTransferStatusToWithDrawn() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, "John Doe's Transfer");
        final var event = new WithdrawalTransferDoneEvent(transferId, sourceId, amount, Instant.now());

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));

        useCase.updateTransferStatus(event);

         verify(transferRepository).findById(transferId);
         verify(transferRepository).save(
                 argThat(t ->
                         Status.WITHDRAWN == t.getStatus() &&
                         t.getDomainEvents().size() == 1
                 )
         );
    }

    @Test
    public void updateTransferStatus_notFoundTransfer_throwsException() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, "John Doe's Transfer");
        final var event = new WithdrawalTransferDoneEvent(transferId, sourceId, amount, Instant.now());

        when(transferRepository.findById(transferId)).thenReturn(Optional.empty());

        final var ex = assertThrows(TransferNotFoundException.class, () -> useCase.updateTransferStatus(event));

        verify(transferRepository).findById(transferId);
        verify(transferRepository, never()).save(any());
        assertEquals("Transfer with id=%s not found".formatted(transferId), ex.getMessage());
    }
}
