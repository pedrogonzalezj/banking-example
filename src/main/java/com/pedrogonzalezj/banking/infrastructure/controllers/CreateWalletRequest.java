
package com.pedrogonzalezj.banking.infrastructure.controllers;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateWalletRequest {
    private String name;
    private UUID ownerId;
}
