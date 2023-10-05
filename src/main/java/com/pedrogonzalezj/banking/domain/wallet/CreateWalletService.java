
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.user.UserNotFoundException;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateWalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public Wallet createWallet(final String name, final UUID ownerId) {
        final var maybeAWallet = walletRepository.findByOwnerIdAndName(ownerId, name);
        if (maybeAWallet.isPresent()) {
            throw new IllegalArgumentException("User with id %s already has a wallet with name=%s".formatted(ownerId, name));
        }
        final var maybeAnUser = userRepository.findById(ownerId);
        if (maybeAnUser.isEmpty()) {
            throw new UserNotFoundException(ownerId);
        }
        final var wallet = maybeAnUser.get().createWallet(name);
        return walletRepository.save(wallet);
    }
}
