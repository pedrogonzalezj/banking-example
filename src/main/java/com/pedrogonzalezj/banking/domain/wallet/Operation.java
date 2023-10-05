
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name="operations")
public class Operation {
    static final String WITHDRAWAL_OPERATION = "[withdrawal] - ";
    static final String DEPOSIT_OPERATION = "[deposit] - ";
    @Id
    @SequenceGenerator(name = "operation_seq", sequenceName = "operation_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_seq")
    private Long id;
    private String message;
    @Column(name = "transfer_id", columnDefinition = "BINARY(16)")
    private UUID transferId;
    @Column(nullable = false)
    private Long amount;
    @Column(name = "wallet_id", columnDefinition = "BINARY(16)")
    private UUID walletId;
    @Column(name = "created_at")
    private Instant createdAt;

    public Operation() {}

    public Operation(final UUID walletId,
                     final String message,
                     final Long amount,
                     final UUID transferId,
                     final Instant createdAt) {
        this.walletId = walletId;
        this.message = message;
        this.amount = amount;
        this.transferId = transferId;
        this.createdAt = createdAt;
    }

    public static Operation withdrawalOperation(final Transfer transfer, final Instant withdrawTime) {
        final var message = WITHDRAWAL_OPERATION + transfer.getMessage();
        return new Operation(transfer.getSource(), message, transfer.getAmount(), transfer.getId(), withdrawTime);
    }

    public static Operation depositOperation(final UUID walletId,
                                             final String message,
                                             final Long amount,
                                             final UUID transferId,
                                             final Instant depositTime) {
        final var compoundMessage = DEPOSIT_OPERATION + message;
        return new Operation(walletId, compoundMessage, amount, transferId, depositTime);
    }
}
