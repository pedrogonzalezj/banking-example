
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.dto.ViewUserDtoAssembler;
import com.pedrogonzalezj.banking.domain.user.User;
import com.pedrogonzalezj.banking.domain.user.UserNotFoundException;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewUserInfoUseCaseTest {
    private ViewUserInfoUseCase useCase;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ViewUserDtoAssembler assembler;

    @BeforeEach
    public void setup() {
        useCase = new ViewUserInfoUseCase(userRepository, assembler);
    }

    @Test
    public void userInfo_returnsMappedStorageUserInfo() {
        final var userId = UUID.randomUUID();
        final var user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        useCase.userInfo(userId);

        verify(userRepository).findById(userId);
        verify(assembler).fromUser(user);
    }

    @Test
    public void userInfo_notFoundUser_throwsException() {
        final var userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        final var ex = assertThrows(UserNotFoundException.class, () -> useCase.userInfo(userId));

        verify(userRepository).findById(userId);
        verify(assembler, never()).fromUser(any());
        assertEquals("User with id=%s not found".formatted(userId), ex.getMessage());
    }
}
