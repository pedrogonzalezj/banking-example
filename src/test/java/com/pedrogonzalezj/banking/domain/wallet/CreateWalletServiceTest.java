
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.user.User;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateWalletServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    private CreateWalletService service;

    private User user;

    @BeforeEach
    public void setup() {
        service = new CreateWalletService(userRepository, walletRepository);
        user = User.registerUser("John", "Doe", "26944814S");
    }

    @Test
    public void createWallet_() {

        final var name = "John Doe's wallet";
        final var ownerId = user.getId();


        when(walletRepository.findByOwnerIdAndName(ownerId, name)).thenReturn(Optional.empty());
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(walletRepository.save(any())).thenReturn(new Wallet());

        service.createWallet(name, ownerId);

        verify(walletRepository).findByOwnerIdAndName(ownerId, name);
        verify(userRepository).findById(ownerId);
        verify(walletRepository).save(
                argThat(w ->
                        w.getId() != null &&
                        name.equals(w.getName()) &&
                        ownerId.equals(w.getOwnerId())
                )
        );
    }
}
