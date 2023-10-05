
package com.pedrogonzalezj.banking.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserService {
    private final UserRepository userRepository;

    public User registerUser(final String name,
                             final String surname,
                             final String email,
                             final String nationalId,
                             final LocalDate birthDate,
                             final String phoneNumber,
                             final Address address) {

        final var userFromDB = userRepository.findByNationalId(nationalId);
        if (userFromDB.isPresent()) {
            throw new IllegalArgumentException("user with NationalId %s already exists.".formatted(nationalId));
        }

        final var user = User.registerUser(name, surname, nationalId);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setBirthDate(birthDate);
        user.setAddress(address);

        return userRepository.save(user);
    }
}
