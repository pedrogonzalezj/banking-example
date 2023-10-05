
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.user.User;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigInteger;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class MakeDepositUseCaseIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private MakeDepositUseCase useCase;
    private UUID walletId;

    @BeforeEach
    public void setup() {
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var user = User.registerUser(name, surname, nationalId);
        userRepository.save(user);
        final var walletName = "John Doe's wallet";
        final var wallet = user.createWallet(walletName);
        walletRepository.save(wallet);
        walletId = wallet.getId();
    }

    @Test
    public void makeDeposit_increasesWalletMoneyWithDepositAmount() {
        useCase.makeDeposit(walletId, 10L);
        final var maybeAWallet = walletRepository.findById(walletId);
        assertTrue(maybeAWallet.isPresent());
        assertEquals(BigInteger.TEN, maybeAWallet.get().getMoney());
    }
}
