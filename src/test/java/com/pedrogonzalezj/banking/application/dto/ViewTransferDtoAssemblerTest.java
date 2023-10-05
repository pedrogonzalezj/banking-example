
package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ViewTransferDtoAssemblerTest {
    private ViewTransferDtoAssembler assembler;

    @BeforeEach
    public void setup() {
        assembler = new ViewTransferDtoAssembler();
    }

    @Test
    public void fromTransfer_returnsAViewTransferDto() {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var amount = 10L;
        final var message = "John's Doe transfer";
        final var updatedAt = Instant.now();
        final var transfer = new Transfer(transferId, sourceId, destinationId, amount, message);
        transfer.finish(updatedAt);

        final var dto = assembler.fromTransfer(transfer);
        assertNotNull(dto);
        assertEquals(transfer.getId(), dto.getId());
        assertEquals(transfer.getSource(), dto.getSource());
        assertEquals(transfer.getDestination(), dto.getDestination());
        assertEquals(transfer.getMessage(), dto.getMessage());
        assertEquals(transfer.getAmount(), dto.getAmount());
        assertEquals(transfer.getStatus().name(), dto.getStatus());
        assertEquals(transfer.getCreatedAt(), dto.getCreatedAt());
        assertEquals(transfer.getUpdatedAt(), dto.getUpdatedAt());
    }
}
