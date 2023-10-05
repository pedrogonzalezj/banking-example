
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.NotedCashWithdrawalEvent;
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
public class DepositTransferMoneyUseCase {
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void depositTransfer(NotedCashWithdrawalEvent event) {
        log.info("[WireTransfer] performing money deposit: {}", event.getTransferId());
        final var maybeATransfer = transferRepository.findById(event.getTransferId());
        if (maybeATransfer.isEmpty()) {
            throw new TransferNotFoundException(event.getTransferId());
        }
        final var transfer = maybeATransfer.get();
        final var maybeAWallet = walletRepository.findById(transfer.getDestination());
        if (maybeAWallet.isEmpty()) {
            throw new WalletNotFoundException(transfer.getDestination());
        }
        final var wallet = maybeAWallet.get();
        wallet.makeDeposit(transfer);
        walletRepository.save(wallet);
        log.info("[WireTransfer] deposit done: {}", transfer);
    }
}
