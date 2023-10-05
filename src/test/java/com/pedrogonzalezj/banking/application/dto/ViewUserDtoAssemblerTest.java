
package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.user.Address;
import com.pedrogonzalezj.banking.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ViewUserDtoAssemblerTest {

    private ViewUserDtoAssembler assembler;

    @BeforeEach
    public void setup() {
        assembler = new ViewUserDtoAssembler();
    }

    @Test
    public void fromUser_onlyRequiredArgs_returnsAViewUserDto() {
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";

        final var user = User.registerUser(name, surname, nationalId);
        final var dto = assembler.fromUser(user);
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getSurname(), dto.getSurname());
        assertEquals(user.getNationalId(), dto.getNationalId());
    }

    @Test
    public void fromUser_withoutAddress_returnsAViewUserDto() {
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var birthDate = LocalDate.of(1999, 12, 1);
        final var email = "johndoes@mail.com";
        final var user = User.registerUser(name, surname, nationalId);
        user.setBirthDate(birthDate);
        user.setEmail(email);
        user.setPhoneNumber("+34 625111111");
        final var dto = assembler.fromUser(user);
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getSurname(), dto.getSurname());
        assertEquals(user.getNationalId(), dto.getNationalId());
        assertEquals(user.getBirthDate(), dto.getBirthDate());
        assertEquals(user.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    public void fromUser_allData_returnsAViewUserDto() {
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var birthDate = LocalDate.of(1999, 12, 1);
        final var email = "johndoes@mail.com";
        final var address = Address.builder()
                .street("r street")
                .number("1")
                .apartmentNumber("23D")
                .zipCode("11111")
                .city("gotham")
                .state("NY")
                .country("US")
                .build();
        final var user = User.registerUser(name, surname, nationalId);
        user.setBirthDate(birthDate);
        user.setEmail(email);
        user.setPhoneNumber("+34 625111111");
        user.setAddress(address);
        final var dto = assembler.fromUser(user);
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getSurname(), dto.getSurname());
        assertEquals(user.getNationalId(), dto.getNationalId());
        assertEquals(user.getBirthDate(), dto.getBirthDate());
        assertEquals(user.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getAddress().getStreet(), dto.getStreet());
        assertEquals(user.getAddress().getNumber(), dto.getNumber());
        assertEquals(user.getAddress().getApartmentNumber(), dto.getApartmentNumber());
        assertEquals(user.getAddress().getZipCode(), dto.getZipCode());
        assertEquals(user.getAddress().getCity(), dto.getCity());
        assertEquals(user.getAddress().getState(), dto.getState());
        assertEquals(user.getAddress().getCountry(), dto.getCountry());
    }
}
