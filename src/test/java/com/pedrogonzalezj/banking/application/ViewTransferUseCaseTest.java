
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.dto.ViewTransferDtoAssembler;
import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewTransferUseCaseTest {
    private ViewTransferUseCase useCase;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private ViewTransferDtoAssembler assembler;

    @BeforeEach
    public void setup() {
        useCase = new ViewTransferUseCase(transferRepository, assembler);
    }

    @Test
    public void transferInfo_returnsMappedStorageTransferInfo() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, "John Doe's Transfer");

        when(transferRepository.findById(transferId)).thenReturn(Optional.of(transfer));

        useCase.transferInfo(transferId);

        verify(transferRepository).findById(transferId);
        verify(assembler).fromTransfer(transfer);
    }

    @Test
    public void transferInfo_notFoundTransfer_throwsException() {
        final var transferId = UUID.randomUUID();

        when(transferRepository.findById(transferId)).thenReturn(Optional.empty());

        assertThrows(TransferNotFoundException.class, () -> useCase.transferInfo(transferId));

        verify(transferRepository).findById(transferId);
        verify(assembler, never()).fromTransfer(any());
    }
}
