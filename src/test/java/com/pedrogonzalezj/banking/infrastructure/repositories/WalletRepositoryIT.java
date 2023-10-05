
package com.pedrogonzalezj.banking.infrastructure.repositories;

import com.pedrogonzalezj.banking.domain.user.User;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import com.pedrogonzalezj.banking.domain.wallet.Operation;
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
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class WalletRepositoryIT {
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

    @Test
    public void save_storesWallet() {
        final var name = "John Doe's wallet";
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        final var wallet = user.createWallet(name);
        wallet.setMoney(BigInteger.TEN);

        walletRepository.save(wallet);

        final var maybeAWallet = walletRepository.findById(wallet.getId());
        assertTrue(maybeAWallet.isPresent());
        final var walletFromDb = maybeAWallet.get();
        assertEquals(wallet.getId(), walletFromDb.getId());
        assertEquals(wallet.getName(), walletFromDb.getName());
        assertEquals(wallet.getMoney(), walletFromDb.getMoney());
        assertEquals(wallet.getOwnerId(), walletFromDb.getOwnerId());
    }

    @Test
    public void save_updatesWallet() {
        final var initialAmount = BigInteger.ZERO;
        final var name = "John Doe's wallet";
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        final var wallet = user.createWallet(name);
        wallet.setMoney(initialAmount);
        walletRepository.save(wallet);

        var maybeAWallet = walletRepository.findById(wallet.getId());
        assertTrue(maybeAWallet.isPresent());
        final var sameWallet = maybeAWallet.get();
        assertEquals(initialAmount, sameWallet.getMoney());

        sameWallet.setMoney(BigInteger.TEN);
        walletRepository.save(sameWallet);
        maybeAWallet = walletRepository.findById(wallet.getId());
        assertTrue(maybeAWallet.isPresent());
        assertEquals(sameWallet.getMoney(), maybeAWallet.get().getMoney());
    }

    @Test
    public void save_storesOperations() {
        final var name = "John Doe's wallet";
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        final var wallet = user.createWallet(name);
        final var operation = Operation.depositOperation(wallet.getId(),"something", 10L, null, Instant.now());
        wallet.getOperations().add(operation);

        walletRepository.save(wallet);

        final var maybeAWallet = walletRepository.findById(wallet.getId());
        assertTrue(maybeAWallet.isPresent());
        final var walletFromDb = maybeAWallet.get();
        assertNotNull(walletFromDb.getOperations());
        assertEquals(1, walletFromDb.getOperations().size());
        assertThat(walletFromDb.getOperations(), hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("message", equalTo(operation.getMessage())),
                hasProperty("amount", equalTo(operation.getAmount())),
                hasProperty("transferId", equalTo(operation.getTransferId())),
                hasProperty("walletId", equalTo(operation.getWalletId())),
                hasProperty("createdAt", equalTo(operation.getCreatedAt()))
        )));
    }

    @Test
    public void save_deletesOperations() {
        final var name = "John Doe's wallet";
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        final var wallet = user.createWallet(name);
        final var operationA = Operation.depositOperation(wallet.getId(),"somethingA", 10L, null, Instant.now());
        final var operationB = Operation.depositOperation(wallet.getId(),"somethingB", 5L, null, Instant.now());
        wallet.getOperations().add(operationA);
        wallet.getOperations().add(operationB);

        walletRepository.save(wallet);

        var maybeAWallet = walletRepository.findById(wallet.getId());
        assertTrue(maybeAWallet.isPresent());
        final var walletFromDb = maybeAWallet.get();
        assertNotNull(walletFromDb.getOperations());
        assertEquals(2, walletFromDb.getOperations().size());

        walletFromDb.getOperations().clear();
        maybeAWallet = walletRepository.findById(wallet.getId());
        assertTrue(maybeAWallet.isPresent());
        assertEquals(0, maybeAWallet.get().getOperations().size());
    }

    @Test
    public void findByOwnerIdAndName() {
        final var name = "John Doe's wallet";
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        final var wallet = user.createWallet(name);
        walletRepository.save(wallet);

        var maybeAWallet = walletRepository.findByOwnerIdAndName(user.getId(), name);
        assertTrue(maybeAWallet.isPresent());
        assertEquals(wallet.getId(), maybeAWallet.get().getId());
    }
}
