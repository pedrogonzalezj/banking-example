
package com.pedrogonzalezj.banking.infrastructure.repositories;

import com.pedrogonzalezj.banking.domain.transfer.TransferRepository;
import com.pedrogonzalezj.banking.domain.user.User;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class TransferRepositoryIT {

    @Container
    static MariaDBContainer mariadb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.11"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransferRepository transferRepository;

    @Test
    public void save_storesTransfer() {
        final var sourceName = "John Doe's wallet";
        final var targetName = "Jane Doe's wallet";
        final var john = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(john);
        final var jane = User.registerUser("Jane", "Doe", "54792696A");
        userRepository.save(jane);
        final var source = john.createWallet(sourceName);
        source.setMoney(BigInteger.TEN);
        walletRepository.save(source);
        final var destination = jane.createWallet(targetName);
        destination.setMoney(BigInteger.ZERO);
        walletRepository.save(destination);

        final var transfer = source.wireTransfer(destination, "transfer test", 10L);
        transferRepository.save(transfer);

        final var maybeATransfer = transferRepository.findById(transfer.getId());
        assertTrue(maybeATransfer.isPresent());
        final var transferFromDb = maybeATransfer.get();
        assertEquals(transfer.getId(), transferFromDb.getId());
        assertEquals(transfer.getSource(), transferFromDb.getSource());
        assertEquals(transfer.getDestination(), transferFromDb.getDestination());
        assertEquals(transfer.getAmount(), transferFromDb.getAmount());
        assertEquals(transfer.getMessage(), transferFromDb.getMessage());
        assertEquals(transfer.getCreatedAt(), transferFromDb.getCreatedAt());
        assertEquals(transfer.getUpdatedAt(), transferFromDb.getUpdatedAt());
        assertEquals(transfer.getStatus(), transferFromDb.getStatus());
    }
}
