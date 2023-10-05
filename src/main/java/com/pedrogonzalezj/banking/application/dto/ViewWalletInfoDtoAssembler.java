package com.pedrogonzalezj.banking.application.dto;

import com.pedrogonzalezj.banking.domain.wallet.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.Collection;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class ViewWalletInfoDtoAssembler {
    private final ViewWalletOperationInfoDtoAssembler operationInfoDtoAssembler;

    public ViewWalletInfoDto fromWallet(Wallet wallet) {
        final var operations = Stream.ofNullable(wallet.getOperations())
                .flatMap(Collection::stream)
                .map(operationInfoDtoAssembler::fromWalletOperation)
                .toList();
        return ViewWalletInfoDto.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .ownerId(wallet.getOwnerId())
                .money(wallet.getMoney())
                .operations(operations)
                .build();
    }
}
