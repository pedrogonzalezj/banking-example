
package com.pedrogonzalezj.banking.application;


import com.pedrogonzalezj.banking.domain.wallet.CreateWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWalletUseCase {
    private final CreateWalletService createWalletService;

    @Transactional
    public UUID createWallet(final String name, final UUID ownerId) {
        log.info("[CreateWallet] creating wallet with name {} for user {}", name, ownerId);
        final var wallet = createWalletService.createWallet(name, ownerId);
        log.info("[CreateWallet] wallet created {}", wallet);
        return wallet.getId();
    }
}
