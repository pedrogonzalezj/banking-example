
package com.pedrogonzalezj.banking.infrastructure.controllers;

import lombok.Data;

import java.util.UUID;

@Data
public class WireTransferRequest {
    private UUID source;
    private UUID destination;
    private String message;
    private Long amount;
}
