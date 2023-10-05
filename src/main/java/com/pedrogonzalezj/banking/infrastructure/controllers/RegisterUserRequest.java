
package com.pedrogonzalezj.banking.infrastructure.controllers;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterUserRequest {
    private String name;
    private String surname;
    private String nationalId;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String street;
    private String number;
    private String apartmentNumber;
    private String zipCode;
    private String city;
    private String state;
    private String country;
}
