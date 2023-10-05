
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.domain.wallet.CreateWalletService;
import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateWalletUseCaseTest {

    private CreateWalletUseCase useCase;
    @Mock
    private CreateWalletService createWalletService;

    @BeforeEach
    public void setup() {
        useCase = new CreateWalletUseCase(createWalletService);
    }

    @Test
    public void createWallet_returnsNewWalletId() {
        final var name = "John Doe's wallet";
        final var ownerId = UUID.randomUUID();
        final var wallet = new Wallet(UUID.randomUUID(), name, ownerId);
        when(createWalletService.createWallet(name, ownerId)).thenReturn(wallet);

        final var walletId = useCase.createWallet(name, ownerId);

        verify(createWalletService).createWallet(name, ownerId);
        assertEquals(wallet.getId(), walletId);
    }
}
