
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.WithdrawalTransferDoneEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalTransferDoneUseCase {
    private final TransferRepository transferRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateTransferStatus(WithdrawalTransferDoneEvent event) {
        log.info("[WireTransfer] updating transfer: {} status", event.getTransferId());
        final var maybeATransfer = transferRepository.findById(event.getTransferId());
        if (maybeATransfer.isEmpty()) {
            throw new TransferNotFoundException(event.getTransferId());
        }
        final var transfer = maybeATransfer.get();
        transfer.putInWithdrawnMoney(event.getCreatedAt());
        transferRepository.save(transfer);
        log.info("[WireTransfer] transfer: {} status updated", event.getTransferId());
    }
}
