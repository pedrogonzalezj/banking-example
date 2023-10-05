
package com.pedrogonzalezj.banking.domain.transfer;

import com.pedrogonzalezj.banking.domain.Event;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "transfers")
public class Transfer implements Serializable {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    @EqualsAndHashCode.Include
    private UUID id;
    @Column(name = "source_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID source;
    @Column(name = "destination_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID destination;
    private String message;
    @Column(nullable = false)
    private Long amount;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Transient
    private List<Event> domainEvents;

    public Transfer() {}

    public Transfer(final UUID id,
                    final UUID source,
                    final UUID destination,
                    final Long amount,
                    final String message
                    ) {

        this.id = id;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.message = message;
        createdAt = Instant.now();
        status = Status.CREATED;
    }

    public void putInWithdrawnMoney(final Instant when) {
        status = Status.WITHDRAWN;
        updatedAt = when;
        events().add(new NotedCashWithdrawalEvent(id, source, amount, Instant.now()));
    }

    public void finish(final Instant when) {
        status = Status.FINISHED;
        updatedAt = when;
        events().add(new TransferPerformedEvent(id, Instant.now()));
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
}
