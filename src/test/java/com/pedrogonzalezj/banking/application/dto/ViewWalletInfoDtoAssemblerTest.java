
package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.wallet.Operation;
import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ViewWalletInfoDtoAssemblerTest {
    private ViewWalletInfoDtoAssembler assembler;
    @Mock
    private ViewWalletOperationInfoDtoAssembler operationInfoDtoAssembler;

    @BeforeEach
    public void setup() {
        assembler = new ViewWalletInfoDtoAssembler(operationInfoDtoAssembler);
    }

    @Test
    public void fromWallet_withNoOperations_returnsAViewWalletInfoDto() {
        final var walletId = UUID.randomUUID();
        final var ownerId = UUID.randomUUID();
        final var name = "John Doe's wallet";
        final var wallet = new Wallet(walletId, name, ownerId);
        wallet.setMoney(BigInteger.TEN);
        final var dto = assembler.fromWallet(wallet);
        assertNotNull(dto);
        assertEquals(wallet.getId(), dto.getId());
        assertEquals(wallet.getName(), dto.getName());
        assertEquals(wallet.getOwnerId(), dto.getOwnerId());
        assertEquals(wallet.getMoney(), dto.getMoney());
        assertNotNull(dto.getOperations());
        assertTrue(dto.getOperations().isEmpty());
    }

    @Test
    public void fromWallet_withOperations_delegatesOperationsMappings() {

        final var walletId = UUID.randomUUID();
        final var ownerId = UUID.randomUUID();
        final var name = "John Doe's wallet";
        final var operationMessage = "john's deposit";
        final var operationAmount = 10L;
        final var wallet = new Wallet(walletId, name, ownerId);
        wallet.setMoney(BigInteger.TEN);
        final var operation = Operation.depositOperation(walletId, operationMessage, operationAmount, UUID.randomUUID(), Instant.now());
        wallet.getOperations().add(operation);
        final var operationInfoDto = ViewWalletInfoDto.ViewWalletOperationInfoDto.builder().message(operationMessage).amount(operationAmount).build();

        when(operationInfoDtoAssembler.fromWalletOperation(operation)).thenReturn(operationInfoDto);

        final var dto = assembler.fromWallet(wallet);
        assertNotNull(dto);
        assertNotNull(dto.getOperations());
        assertEquals(1, dto.getOperations().size());
        verify(operationInfoDtoAssembler).fromWalletOperation(operation);
    }
}
