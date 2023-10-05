
package com.pedrogonzalezj.banking.application;


import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDtoAssembler;
import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import com.pedrogonzalezj.banking.domain.wallet.WalletNotFoundException;
import com.pedrogonzalezj.banking.domain.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewWalletInfoUseCaseTest {
    private ViewWalletInfoUseCase useCase;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private ViewWalletInfoDtoAssembler assembler;

    @BeforeEach
    public void setup() {
        useCase = new ViewWalletInfoUseCase(walletRepository, assembler);
    }

    @Test
    public void walletInfo_returnsMappedStorageWalletInfo() {
        final var walletId = UUID.randomUUID();
        final var destination = new Wallet(walletId, "Jane Doe's wallet", UUID.randomUUID());

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(destination));

        useCase.walletInfo(walletId);

        verify(walletRepository).findById(walletId);
        verify(assembler).fromWallet(destination);
    }

    @Test
    public void walletInfo_notFoundWallet_throwsException() {
        final var walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        final var ex = assertThrows(WalletNotFoundException.class, () -> useCase.walletInfo(walletId));

        verify(walletRepository).findById(walletId);
        verify(assembler, never()).fromWallet(any());
        assertEquals("Wallet with id=%s not found".formatted(walletId), ex.getMessage());
    }
}
