
package com.pedrogonzalezj.banking.domain.user;

import lombok.Getter;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    @Getter private final UUID userId;

    public UserNotFoundException(final UUID userId) {
        super("User with id=%s not found".formatted(userId));
        this.userId = userId;
    }
}
