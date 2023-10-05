
package com.pedrogonzalezj.banking.infrastructure.repositories;

import com.pedrogonzalezj.banking.domain.user.Address;
import com.pedrogonzalezj.banking.domain.user.User;
import com.pedrogonzalezj.banking.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("it")
@Transactional
public class UserRepositoryIT {
    @Container
    static MariaDBContainer mariadb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.11"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {}

    @Test
    public void save_storesUserData() {
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        final var maybeAnUser = userRepository.findById(user.getId());
        assertTrue(maybeAnUser.isPresent());
        final var userFromDb = maybeAnUser.get();
        assertEquals(user.getName(), userFromDb.getName());
        assertEquals(user.getSurname(), userFromDb.getSurname());
        assertEquals(user.getNationalId(), userFromDb.getNationalId());
    }

    @Test
    public void save_updatesUserData() {
        final var address = Address.builder()
                .street("random street 123")
                .apartmentNumber("2A")
                .city("gotham")
                .build();
        final var birthDate = LocalDate.of(2023, 11, 15);
        final var phoneNumber = "¡34 625676767";
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);
        assertNull(user.getAddress());
        assertNull(user.getBirthDate());
        assertNull(user.getPhoneNumber());

        user.setAddress(address);
        user.setBirthDate(birthDate);
        user.setPhoneNumber(phoneNumber);

        userRepository.save(user);
        final var maybeAnUser = userRepository.findById(user.getId());

        assertTrue(maybeAnUser.isPresent());
        final var userFromDb = maybeAnUser.get();
        assertNotNull(userFromDb.getAddress());
        assertEquals(address.getStreet(), userFromDb.getAddress().getStreet());
        assertEquals(address.getApartmentNumber(), userFromDb.getAddress().getApartmentNumber());
        assertEquals(address.getCity(), userFromDb.getAddress().getCity());
        assertEquals(birthDate, userFromDb.getBirthDate());
        assertEquals(phoneNumber, userFromDb.getPhoneNumber());
    }

    @Test
    public void findByNationalId_returnsUser() {
        final var user = User.registerUser("John", "Doe", "26944814S");
        userRepository.save(user);

        final var maybeAnUser = userRepository.findByNationalId(user.getNationalId());

        assertTrue(maybeAnUser.isPresent());
        assertEquals(user.getId(), maybeAnUser.get().getId());
    }
}
