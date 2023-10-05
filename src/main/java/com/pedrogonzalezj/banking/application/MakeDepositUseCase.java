
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDto;
import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDtoAssembler;
import com.pedrogonzalezj.banking.domain.wallet.WalletNotFoundException;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MakeDepositUseCase {
    private final WalletRepository walletRepository;
    private final ViewWalletInfoDtoAssembler viewWalletInfoDtoAssembler;

    @Transactional
    public ViewWalletInfoDto makeDeposit(final UUID walletId, final Long amount) {
        log.info("[Deposit] making deposit: {} with amount: {}", walletId, amount);
        final var maybeAWallet = walletRepository.findById(walletId);
        if (maybeAWallet.isEmpty()) {
            throw new WalletNotFoundException(walletId);
        }
        final var wallet = maybeAWallet.get();
        wallet.makeDeposit(amount);
        walletRepository.save(wallet);
        log.info("[Deposit] deposit done for wallet: {} with amount: {}", walletId, amount);
        return viewWalletInfoDtoAssembler.fromWallet(wallet);
    }
}
