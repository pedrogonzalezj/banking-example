
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.command.RegisterUserCommand;
import com.pedrogonzalezj.banking.domain.user.Address;
import com.pedrogonzalezj.banking.domain.user.RegisterUserService;
import com.pedrogonzalezj.banking.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseTest {
    private RegisterUserUseCase useCase;
    @Mock
    private RegisterUserService service;

    @BeforeEach
    public void setup() {
        useCase = new RegisterUserUseCase(service);
    }

    @Test
    public void makeDeposit_depositAmountIntoWallet() {

        final var command = RegisterUserCommand.builder()
                .name("John")
                .surname("Doe")
                .nationalId("26944814S")
                .build();

        final var user = User.registerUser(command.getName(), command.getSurname(), command.getNationalId());

        when(service.registerUser(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any())
        ).thenReturn(user);

        final var userId = useCase.registerUser(command);

        verify(service).registerUser(
                eq(command.getName()),
                eq(command.getSurname()),
                eq(command.getEmail()),
                eq(command.getNationalId()),
                eq(command.getBirthDate()),
                eq(command.getPhoneNumber()),
                nullable(Address.class)
        );
        assertEquals(user.getId(), userId);
    }
}
