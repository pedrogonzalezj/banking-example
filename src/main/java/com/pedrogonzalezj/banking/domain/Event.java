
package com.pedrogonzalezj.banking.domain;


import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class Event {
    private final UUID id;
    private final Instant createdAt;

    public Event(Instant createdAt) {
        this.id = UUID.randomUUID();
        this.createdAt = createdAt;
    }
}
