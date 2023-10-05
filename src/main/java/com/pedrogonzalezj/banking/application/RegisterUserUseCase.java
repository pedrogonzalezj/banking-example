
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.command.RegisterUserCommand;
import com.pedrogonzalezj.banking.domain.user.Address;
import com.pedrogonzalezj.banking.domain.user.RegisterUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final RegisterUserService registerUserService;

    @Transactional
    public UUID registerUser(RegisterUserCommand command) {
        log.info("[Register] registering user: {}", command.getName());
        final var address = Address.builder()
                .street(command.getStreet())
                .number(command.getNumber())
                .apartmentNumber(command.getApartmentNumber())
                .zipCode(command.getZipCode())
                .city(command.getCity())
                .state(command.getState())
                .country(command.getCountry())
                .build();
        final var user = registerUserService.registerUser(
                command.getName(),
                command.getSurname(),
                command.getEmail(),
                command.getNationalId(),
                command.getBirthDate(),
                command.getPhoneNumber(),
                address
        );
        log.info("[Register] user registered: {}", user);
        return user.getId();
    }
}
