package com.pedrogonzalezj.banking.application.command;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@RequiredArgsConstructor
public class RegisterUserCommand {
    private final String name;
    private final String surname;
    private final String nationalId;
    private final LocalDate birthDate;
    private final String email;
    private final String phoneNumber;
    private final String street;
    private final String number;
    private final String apartmentNumber;
    private final String zipCode;
    private final String city;
    private final String state;
    private final String country;
}
