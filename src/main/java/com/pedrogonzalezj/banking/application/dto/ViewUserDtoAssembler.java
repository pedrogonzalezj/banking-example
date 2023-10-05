package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class ViewUserDtoAssembler {

    public ViewUserDto fromUser(final User user) {
        final var builder = ViewUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .nationalId(user.getNationalId())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber());

        if (user.getAddress() != null) {
            builder.street(user.getAddress().getStreet())
                    .number(user.getAddress().getNumber())
                    .apartmentNumber(user.getAddress().getApartmentNumber())
                    .zipCode(user.getAddress().getZipCode())
                    .city(user.getAddress().getCity())
                    .state(user.getAddress().getState())
                    .country(user.getAddress().getCountry());
        }
        return builder.build();
    }
}
