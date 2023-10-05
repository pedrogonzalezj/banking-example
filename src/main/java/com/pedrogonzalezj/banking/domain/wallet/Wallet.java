
package com.pedrogonzalezj.banking.domain.wallet;

import com.pedrogonzalezj.banking.domain.Event;
import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import com.pedrogonzalezj.banking.domain.transfer.TransferCreatedEvent;
import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Wallet implements Serializable {
    static final String DEPOSIT_MESSAGE = "owner deposit";
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String name;
    @Column(name = "owner_id", nullable = false, columnDefinition = "BINARY(16)")
    @EqualsAndHashCode.Include
    private UUID ownerId;
    @Column(nullable = false)
    @Setter
    private BigInteger money;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private final List<Operation> operations = new ArrayList<>();
    @Transient
    private List<Event> domainEvents;

    public Wallet() {
        money = BigInteger.ZERO;
    }

    public Wallet(final UUID id, final String name, final UUID ownerId) {
        this();
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }

    public Transfer wireTransfer(
            final Wallet destination,
            final String message,
            final Long amount) {

        if (destination == null) {
            throw new IllegalArgumentException("Destination wallet is required");
        }
        validateWithdrawalAmount(amount);
        final var transferId = UUID.randomUUID();
        final var transfer = new Transfer(transferId, id, destination.getId(), amount, message);
        transfer.events().add(new TransferCreatedEvent(
                transferId,
                id,
                destination.getId(),
                amount,
                message,
                transfer.getCreatedAt()
        ));
        return transfer;
    }

    public void makeDeposit(final Transfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("deposit transfer is required");
        }
        if (!id.equals(transfer.getDestination())) {
            throw new IllegalArgumentException("transfer target: %s is not the same as current wallet: %s".formatted(transfer.getDestination(), id));
        }
        final var amount = transfer.getAmount();
        makeDeposit(transfer.getId(), amount, transfer.getMessage());
        events().add(new MoneyTransferDepositedEvent(transfer.getId(), id, amount, Instant.now()));
    }

    public void makeDeposit(final Long amount) {
        makeDeposit(null, amount, DEPOSIT_MESSAGE);
        events().add(new DepositDoneEvent(id, amount, Instant.now()));
    }

    public void withdrawMoney(final Transfer transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("withdrawal transfer is required");
        }
        if (!id.equals(transfer.getSource())) {
            throw new IllegalArgumentException("withdraw source: %s is not the same as current wallet: %s".formatted(transfer.getSource(), id));
        }
        final var amount = transfer.getAmount();
        validateWithdrawalAmount(amount);
        money = money.subtract(BigInteger.valueOf(amount));
        final var withdrawTime = Instant.now();
        operations.add(Operation.withdrawalOperation(transfer, withdrawTime));
        events().add(new WithdrawalTransferDoneEvent(transfer.getId(), id, amount, withdrawTime));
    }

    @DomainEvents
    public List<Event> events() {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        return domainEvents;
    }

    @AfterDomainEventPublication
    public void clearEvents() {
        domainEvents.clear();
    }

    private void validateDepositAmount(Long amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount must be provided for a deposit");
        }
        if (amount.compareTo(0L) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
    }

    private void makeDeposit(UUID transferId, Long amount, String message) {
        validateDepositAmount(amount);
        money = money.add(BigInteger.valueOf(amount));
        final var depositTime = Instant.now();
        operations.add(Operation.depositOperation(id, message, amount, transferId, depositTime));

    }

    private void validateWithdrawalAmount(Long amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must be provided");
        }
        if (amount.compareTo(0L) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (money.compareTo(BigInteger.valueOf(amount)) < 0) {
            throw new IllegalArgumentException("Can not withdraw more money than current savings");
        }
    }
}
