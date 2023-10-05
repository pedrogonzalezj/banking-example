package com.pedrogonzalezj.banking.application.dto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ViewTransferDto {
    private UUID id;
    private UUID source;
    private UUID destination;
    private String message;
    private Long amount;
    private Instant createdAt;
    private Instant updatedAt;
    private String status;
}
