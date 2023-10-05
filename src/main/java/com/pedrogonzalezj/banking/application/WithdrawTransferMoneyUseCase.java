
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.TransferCreatedEvent;
import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.WalletNotFoundException;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
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
public class WithdrawTransferMoneyUseCase {
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void withdrawTransferMoney(TransferCreatedEvent event) {
        log.info("[WireTransfer] performing transfer: {} amount withdrawal", event.getTransferId());
        final var maybeAWallet = walletRepository.findById(event.getSource());
        if (maybeAWallet.isEmpty()) {
            throw new WalletNotFoundException(event.getSource());
        }
        final var maybeATransfer = transferRepository.findById(event.getTransferId());
        if (maybeATransfer.isEmpty()) {
            throw new TransferNotFoundException(event.getTransferId());
        }
        final var transfer = maybeATransfer.get();
        final var wallet = maybeAWallet.get();
        wallet.withdrawMoney(transfer);
        walletRepository.save(wallet);
        log.info("[WireTransfer] transfer: {} amount withdrawn", transfer);
    }
}
