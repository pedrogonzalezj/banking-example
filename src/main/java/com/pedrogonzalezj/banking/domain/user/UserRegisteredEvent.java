
package com.pedrogonzalezj.banking.domain.user;

import com.pedrogonzalezj.banking.domain.Event;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserRegisteredEvent extends Event {
    private final UUID userId;
    private final String name;
    private final String surname;
    private final String nationalId;

    public UserRegisteredEvent(final UUID userId,
                               final String name,
                               final String surname,
                               final String nationalId,
                               final Instant createdAt) {
        super(createdAt);
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.nationalId = nationalId;
    }
}
