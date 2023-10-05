
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.application.RegisterUserUseCase;
import com.pedrogonzalezj.banking.application.ViewUserInfoUseCase;
import com.pedrogonzalezj.banking.application.dto.ViewUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;
    @MockBean
    private ViewUserInfoUseCase viewUserInfoUseCase;


    @Test
    public void register_onlyRequiredData_returnsUserId() throws Exception {
        final var userId = UUID.randomUUID();
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var link = linkTo(methodOn(UserController.class).get(userId)).toString();
        final var requestBody = """
                {
                  "name": "%s",
                  "surname": "%s",
                  "nationalId": "%s"
                }
                """.formatted(name, surname, nationalId);

        when(registerUserUseCase.registerUser(any())).thenReturn(userId);

        mockMvc.perform(post("/users").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(userId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.created.href").value(link));

        verify(registerUserUseCase).registerUser(
                argThat(u ->
                        name.equals(u.getName()) &&
                        surname.equals(u.getSurname()) &&
                        nationalId.equals(u.getNationalId())
                )
        );
    }

    @Test
    public void register_allData_returnsUserId() throws Exception {
        final var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final var userId = UUID.randomUUID();
        final var link = linkTo(methodOn(UserController.class).get(userId)).toString();
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var birthDate = "1999-12-15";
        final var email = "test@mail.com";
        final var phoneNumber = "+34 625111111";
        final var street = "random street";
        final var number = "123";
        final var apartmentNumber = "12D";
        final var zipCode = "X2312";
        final var city = "gotham";
        final var state = "NY";
        final var country = "US";
        final var requestBody = """
                {
                  "name": "%s",
                  "surname": "%s",
                  "nationalId": "%s",
                  "birthDate": "%s",
                  "email": "%s",
                  "phoneNumber": "%s",
                  "street": "%s",
                  "number": "%s",
                  "apartmentNumber": "%s",
                  "zipCode": "%s",
                  "city": "%s",
                  "state": "%s",
                  "country": "%s"
                }
                """.formatted(name, surname, nationalId, birthDate, email, phoneNumber, street, number, apartmentNumber, zipCode, city, state, country);
        when(registerUserUseCase.registerUser(any())).thenReturn(userId);

        mockMvc.perform(post("/users").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(userId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.created.href").value(link));

        verify(registerUserUseCase).registerUser(
                argThat(u ->
                        name.equals(u.getName()) &&
                        surname.equals(u.getSurname()) &&
                        nationalId.equals(u.getNationalId()) &&
                        birthDate.equals(u.getBirthDate().format(dateFormatter)) &&
                        email.equals(u.getEmail()) &&
                        phoneNumber.equals(u.getPhoneNumber()) &&
                        street.equals(u.getStreet()) &&
                        number.equals(u.getNumber()) &&
                        apartmentNumber.equals(u.getApartmentNumber()) &&
                        zipCode.equals(u.getZipCode()) &&
                        city.equals(u.getCity()) &&
                        state.equals(u.getState()) &&
                        country.equals(u.getCountry())
                )
        );
    }

    @Test
    public void get_returnsViewUserInfoDto() throws Exception {
        final var userId = UUID.randomUUID();
        final var link = linkTo(methodOn(UserController.class).get(userId)).toString();
        final var name = "John";
        final var surname = "Doe";
        final var nationalId = "26944814S";
        final var birthDate = "1999-12-15";
        final var email = "test@mail.com";
        final var phoneNumber = "+34 625111111";
        final var street = "random street";
        final var number = "123";
        final var apartmentNumber = "12D";
        final var zipCode = "X2312";
        final var city = "gotham";
        final var state = "NY";
        final var country = "US";
        final var dto = ViewUserDto.builder()
                .id(userId)
                .name(name)
                .surname(surname)
                .nationalId(nationalId)
                .birthDate(LocalDate.parse(birthDate))
                .email(email)
                .phoneNumber(phoneNumber)
                .street(street)
                .number(number)
                .apartmentNumber(apartmentNumber)
                .zipCode(zipCode)
                .city(city)
                .state(state)
                .country(country)
                .build();

        when(viewUserInfoUseCase.userInfo(any())).thenReturn(dto);

        mockMvc.perform(get("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value(name))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.surname").value(surname))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.nationalId").value(nationalId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.birthDate").value(birthDate))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.email").value(email))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.phoneNumber").value(phoneNumber))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.street").value(street))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.number").value(number))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.apartmentNumber").value(apartmentNumber))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.zipCode").value(zipCode))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.city").value(city))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.state").value(state))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.country").value(country))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.self.href").value(link));

        verify(viewUserInfoUseCase).userInfo(userId);
    }
}
