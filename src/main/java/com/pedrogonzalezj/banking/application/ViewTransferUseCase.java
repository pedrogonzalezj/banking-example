
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.dto.ViewTransferDto;
import com.pedrogonzalezj.banking.application.dto.ViewTransferDtoAssembler;
import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewTransferUseCase {
    private final TransferRepository transferRepository;
    private final ViewTransferDtoAssembler assembler;

    public ViewTransferDto transferInfo(final UUID transferId) {
        log.info("[ViewTransfer] preparing transfer: {} data", transferId);
        final var maybeATransfer = transferRepository.findById(transferId);
        if (maybeATransfer.isEmpty()) {
            throw new TransferNotFoundException(transferId);
        }
        final var transfer = maybeATransfer.get();
        log.info("[ViewTransfer] transfer data prepared: {}", transfer);
        return assembler.fromTransfer(transfer);
    }
}
