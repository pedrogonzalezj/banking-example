package com.pedrogonzalezj.banking.application;


import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class WireTransferUseCase {
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;

    @Transactional
    public UUID wireTransfer(
            final UUID sourceId,
            final UUID destinationId,
            final String message,
            final Long amount) {

        log.info("[WireTransfer] performing transfer. Source: {} and Destination: {}", sourceId, destinationId);
        final var maybeAWallet = walletRepository.findById(sourceId);
        if (maybeAWallet.isEmpty()) {
            throw new IllegalArgumentException("source wallet is required.");
        }
        final var source = maybeAWallet.get();
        final var destination = walletRepository.findById(destinationId).orElse(null);

        final var transfer = source.wireTransfer(destination, message, amount);
        transferRepository.save(transfer);
        log.info("[WireTransfer] transfer done: {}", transfer);
        return transfer.getId();
    }
}
