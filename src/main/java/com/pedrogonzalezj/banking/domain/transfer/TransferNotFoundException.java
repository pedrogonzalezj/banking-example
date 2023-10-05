
package com.pedrogonzalezj.banking.domain.transfer;

import lombok.Getter;

import java.util.UUID;

public class TransferNotFoundException extends RuntimeException {
    @Getter
    private final UUID transferId;

    public TransferNotFoundException(final UUID transferId) {
        super("Transfer with id=%s not found".formatted(transferId));
        this.transferId = transferId;
    }
}
