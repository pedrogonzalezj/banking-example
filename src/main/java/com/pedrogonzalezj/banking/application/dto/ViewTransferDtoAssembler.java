package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.transfer.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ViewTransferDtoAssembler {

    public ViewTransferDto fromTransfer(final Transfer transfer) {
        return ViewTransferDto.builder()
                .id(transfer.getId())
                .source(transfer.getSource())
                .destination(transfer.getDestination())
                .message(transfer.getMessage())
                .amount(transfer.getAmount())
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt())
                .status(transfer.getStatus() != null ? transfer.getStatus().name() : null)
                .build();
    }
}
