
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.command.RegisterUserCommand;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class RegisterUserUseCaseIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegisterUserUseCase useCase;

    @Test
    public void registerUser_onlyRequiredData_createsNewUser() {
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var command = RegisterUserCommand.builder()
                .name(name)
                .surname(surname)
                .nationalId(nationalId)
                .build();
        final var userId = useCase.registerUser(command);
        assertNotNull(userId);
        final var maybeAnUser = userRepository.findById(userId);
        assertTrue(maybeAnUser.isPresent());
        final var user = maybeAnUser.get();
        assertEquals(userId, user.getId());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals(nationalId, user.getNationalId());
    }
}
