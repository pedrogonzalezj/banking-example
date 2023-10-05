
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.transfer.Status;
import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class WireTransferUseCaseIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private WireTransferUseCase useCase;
    private UUID sourceId;
    private UUID destinationId;

    @BeforeEach
    public void setup() {
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var user = User.registerUser(name, surname, nationalId);
        userRepository.save(user);
        final var sourceWalletName = "John Doe's first wallet";
        final var source = user.createWallet(sourceWalletName);
        source.setMoney(BigInteger.valueOf(100L));
        walletRepository.save(source);
        sourceId = source.getId();
        final var destinationWalletName = "John Doe's second wallet";
        final var destination = user.createWallet(destinationWalletName);
        walletRepository.save(destination);
        destinationId = destination.getId();
    }

    @Test
    public void wireTransfer_createsNewTransfer() {
        final var message = "John Doe's transfer";
        final var transferId = useCase.wireTransfer(sourceId, destinationId, message, 10L);
        final var maybeATransfer = transferRepository.findById(transferId);
        assertTrue(maybeATransfer.isPresent());
        final var transfer = maybeATransfer.get();
        assertEquals(transferId, transfer.getId());
        assertEquals(sourceId, transfer.getSource());
        assertEquals(destinationId, transfer.getDestination());
        assertEquals(Status.CREATED, transfer.getStatus());
        assertEquals(10L, transfer.getAmount());
    }
}
