
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

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MakeDepositUseCaseTest {
    private MakeDepositUseCase useCase;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private ViewWalletInfoDtoAssembler viewWalletInfoDtoAssembler;

    @BeforeEach
    public void setup() {
        useCase = new MakeDepositUseCase(walletRepository, viewWalletInfoDtoAssembler);
    }

    @Test
    public void makeDeposit_depositAmountIntoWallet() {
        final var walletId = UUID.randomUUID();
        final var amount = 10L;
        final var destination = new Wallet(walletId, "Jane Doe's wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.ZERO);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(destination));

        useCase.makeDeposit(walletId, amount);

        verify(walletRepository).findById(walletId);
        verify(viewWalletInfoDtoAssembler).fromWallet(destination);
        verify(walletRepository).save(
                argThat(w ->
                        BigInteger.valueOf(amount).equals(w.getMoney()) &&
                        w.getOperations().size() == 1 &&
                        w.getDomainEvents().size() == 1
                )
        );
    }

    @Test
    public void makeDeposit_notFoundWallet_throwsException() {
        final var walletId = UUID.randomUUID();
        final var amount = 10L;
        final var destination = new Wallet(walletId, "Jane Doe's wallet", UUID.randomUUID());
        destination.setMoney(BigInteger.ZERO);

        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        final var ex = assertThrows(WalletNotFoundException.class, () -> useCase.makeDeposit(walletId, amount));

        verify(walletRepository).findById(walletId);
        verify(viewWalletInfoDtoAssembler, never()).fromWallet(any());
        verify(walletRepository, never()).save(any());
        assertEquals("Wallet with id=%s not found".formatted(walletId), ex.getMessage());
    }
}
