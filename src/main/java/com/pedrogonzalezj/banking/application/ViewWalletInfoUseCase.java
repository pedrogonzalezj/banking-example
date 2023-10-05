
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDto;
import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDtoAssembler;
import com.pedrogonzalezj.banking.domain.wallet.WalletNotFoundException;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewWalletInfoUseCase {
    private final WalletRepository walletRepository;
    private final ViewWalletInfoDtoAssembler assembler;

    public ViewWalletInfoDto walletInfo(final UUID walletId) {
        log.info("[ViewWallet] returning wallet {} data", walletId);
        final var maybeAWallet = walletRepository.findById(walletId);
        if (maybeAWallet.isEmpty()) {
            throw new WalletNotFoundException(walletId);
        }
        return assembler.fromWallet(maybeAWallet.get());
    }
}
