package com.pedrogonzalezj.banking.application.dto;



import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ViewWalletInfoDto {
    private UUID id;
    private String name;
    private UUID ownerId;
    private BigInteger money;
    private List<ViewWalletOperationInfoDto> operations;

    @Data
    @Builder
    public static class ViewWalletOperationInfoDto {
        private String message;
        private UUID transferId;
        private Long amount;
        private Instant createdAt;
    }
}
