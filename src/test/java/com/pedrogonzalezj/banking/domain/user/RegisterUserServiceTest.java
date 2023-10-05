
package com.pedrogonzalezj.banking.domain.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {
    @Mock
    private UserRepository userRepository;
    private RegisterUserService service;

    private User user;

    @BeforeEach
    public void setup() {
        service = new RegisterUserService(userRepository);

        final var address = Address.builder()
                .street("random")
                .number("1")
                .country("ES")
                .build();
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("johndoe@mail.com");
        user.setPhoneNumber("+34666545454");
        user.setNationalId("26944814S");
        user.setBirthDate(LocalDate.of(2023, 1, 1));
        user.setAddress(address);
    }

    @Test
    public void registerUser_storesNewUser() {
        when(userRepository.findByNationalId(user.getNationalId())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        service.registerUser(
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getNationalId(),
                user.getBirthDate(),
                user.getPhoneNumber(),
                user.getAddress()
        );

        verify(userRepository).save(
                argThat(u ->
                        user.getAddress().equals(u.getAddress()) &&
                        user.getBirthDate().equals(u.getBirthDate()) &&
                        user.getEmail().equals(u.getEmail()) &&
                        user.getPhoneNumber().equals(u.getPhoneNumber()) &&
                        user.getNationalId().equals(u.getNationalId()) &&
                        user.getSurname().equals(u.getSurname()) &&
                        user.getName().equals(u.getName()) &&
                        user.getId() != null
                )
        );
    }

    @Test
    public void registerUser_existingNationalId_throwsException() {

        when(userRepository.findByNationalId(user.getNationalId())).thenReturn(Optional.of(user));

        final var ex = assertThrows(
                IllegalArgumentException.class,
                ()-> service.registerUser(
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        user.getNationalId(),
                        user.getBirthDate(),
                        user.getPhoneNumber(),
                        user.getAddress()
                )
        );

        assertEquals("user with NationalId %s already exists.".formatted(user.getNationalId()), ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
