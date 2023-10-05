
package com.pedrogonzalezj.banking.application;

import com.pedrogonzalezj.banking.application.dto.ViewUserDto;
import com.pedrogonzalezj.banking.application.dto.ViewUserDtoAssembler;
import com.pedrogonzalezj.banking.domain.user.UserNotFoundException;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewUserInfoUseCase {
    private final UserRepository userRepository;
    private final ViewUserDtoAssembler assembler;

    public ViewUserDto userInfo(final UUID userId) {
        log.info("[ViewUser] preparing data for user: {}", userId);
        final var maybeAnUser = userRepository.findById(userId);
        if (maybeAnUser.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        final var user = maybeAnUser.get();
        log.info("[ViewUser] returning data: {}", user);
        return assembler.fromUser(user);
    }
}
