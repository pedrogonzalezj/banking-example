
package com.pedrogonzalezj.banking.domain.user;

import com.pedrogonzalezj.banking.domain.Event;
import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import com.pedrogonzalezj.banking.domain.wallet.WalletCreatedEvent;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    @EqualsAndHashCode.Include
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(name = "national_id", nullable = false)
    private String nationalId;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Embedded
    private Address address;
    @Transient
    private List<Event> domainEvents;

    public Wallet createWallet(final String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Wallet name is required");
        }
        final var walletId = UUID.randomUUID();
        final var wallet = new Wallet(walletId, name, id);
        wallet.setMoney(BigInteger.ZERO);
        wallet.events().add(new WalletCreatedEvent(walletId, Instant.now()));
        return wallet;
    }

    public static User registerUser(final String name,
                                    final String surname,
                                    final String nationalId) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("user name is required");
        }
        if (surname == null || surname.isEmpty()) {
            throw new IllegalArgumentException("user surname is required");
        }
        if (nationalId == null || nationalId.isEmpty()) {
            throw new IllegalArgumentException("user national id is required");
        }
        final var id = UUID.randomUUID();
        var user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        user.setNationalId(nationalId);
        user.events().add(new UserRegisteredEvent(user.getId(), name, surname, nationalId,Instant.now()));
        return user;
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
