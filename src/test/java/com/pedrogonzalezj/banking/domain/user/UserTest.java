
package com.pedrogonzalezj.banking.domain.user;
import com.pedrogonzalezj.banking.domain.wallet.WalletCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void registerUser_emptyName_throwsException() {
        final var ex = assertThrows(
                IllegalArgumentException.class,
                () -> User.registerUser(null, "Doe", "26944814S")
        );
        assertEquals("user name is required", ex.getMessage());
    }

    @Test
    public void registerUser_emptySurname_throwsException() {
        final var ex = assertThrows(
                IllegalArgumentException.class,
                () -> User.registerUser("John", null, "26944814S")
        );
        assertEquals("user surname is required", ex.getMessage());
    }

    @Test
    public void registerUser_emptyNationalId_throwsException() {
        final var ex = assertThrows(
                IllegalArgumentException.class,
                () -> User.registerUser("John", "Doe", null)
        );
        assertEquals("user national id is required", ex.getMessage());
    }

    @Test
    public void registerUser_validParameters_createsNewUser() {
        final var username = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var user = User.registerUser(username, surname, nationalId);
        assertEquals(username, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(nationalId, user.getNationalId());
        assertThat(user.getDomainEvents(), hasItem(allOf(
                instanceOf(UserRegisteredEvent.class),
                hasProperty("userId", equalTo(user.getId())),
                hasProperty("name", equalTo(user.getName())),
                hasProperty("surname", equalTo(user.getSurname())),
                hasProperty("nationalId", equalTo(user.getNationalId()))
        )));
    }

    @Test
    public void createWallet_emptyName_throwsException() {
        final var user = User.registerUser("John", "Doe", "26944814S");
        final var ex = assertThrows(
                IllegalArgumentException.class,
                () -> user.createWallet(null)
        );
        assertEquals("Wallet name is required", ex.getMessage());
    }

    @Test
    public void createWallet_emptyName_createsUserWallet() {
        final var walletName = "John's Doe wallet";
        final var user = User.registerUser("John", "Doe", "26944814S");
        final var wallet = user.createWallet(walletName);
        assertNotNull(wallet);
        assertNotNull(wallet.getId());
        assertEquals(walletName, wallet.getName());
        assertEquals(BigInteger.ZERO, wallet.getMoney());
        assertThat(wallet.getDomainEvents(), hasItem(allOf(
                instanceOf(WalletCreatedEvent.class),
                hasProperty("walletId", equalTo(wallet.getId()))
        )));
    }
}
