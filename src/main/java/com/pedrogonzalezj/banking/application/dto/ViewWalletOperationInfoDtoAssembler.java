
package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.wallet.Operation;
import org.springframework.stereotype.Component;

@Component
public class ViewWalletOperationInfoDtoAssembler {

    public ViewWalletInfoDto.ViewWalletOperationInfoDto fromWalletOperation(Operation operation) {
        return ViewWalletInfoDto.ViewWalletOperationInfoDto.builder()
                .message(operation.getMessage())
                .transferId(operation.getTransferId())
                .amount(operation.getAmount())
                .createdAt(operation.getCreatedAt())
                .build();
    }
}
