package com.pedrogonzalezj.banking.application.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ViewUserDto {
    private UUID id;
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
