
package com.pedrogonzalezj.banking.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Address implements Serializable {
    private String street;
    private String number;
    @Column(name = "apartment_number")
    private String apartmentNumber;
    @Column(name = "zip_code")
    private String zipCode;
    private String city;
    private String state;
    private String country;
}
