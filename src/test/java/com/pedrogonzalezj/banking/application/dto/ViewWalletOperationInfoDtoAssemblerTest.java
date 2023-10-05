
package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.wallet.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ViewWalletOperationInfoDtoAssemblerTest {
    private ViewWalletOperationInfoDtoAssembler assembler;

    @BeforeEach
    public void setup() {
        assembler = new ViewWalletOperationInfoDtoAssembler();
    }

    @Test
    public void fromWalletOperation_returnsAViewWalletOperationInfoDto() {
        final var walletId = UUID.randomUUID();
        final var message = "John Doe's deposit";
        final var amount = 10L;
        final var transferId = UUID.randomUUID();
        final var depositTime = Instant.now();
        final var operation = Operation.depositOperation(walletId, message, amount, transferId, depositTime);

        final var dto = assembler.fromWalletOperation(operation);

        assertNotNull(dto);
        assertEquals(operation.getTransferId(), dto.getTransferId());
        assertEquals(operation.getMessage(), dto.getMessage());
        assertEquals(operation.getAmount(), dto.getAmount());
        assertEquals(operation.getCreatedAt(), dto.getCreatedAt());
    }
}
