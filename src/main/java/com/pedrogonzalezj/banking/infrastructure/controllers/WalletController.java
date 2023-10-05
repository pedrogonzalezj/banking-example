
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.application.CreateWalletUseCase;
import com.pedrogonzalezj.banking.application.MakeDepositUseCase;
import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDto;
import com.pedrogonzalezj.banking.application.ViewWalletInfoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Slf4j
@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final ViewWalletInfoUseCase viewWalletInfoUseCase;
    private final MakeDepositUseCase makeDepositUseCase;


    @PostMapping
    public ResponseEntity<EntityModel<GenericResponse<UUID>>> create(@RequestBody CreateWalletRequest req) {
        log.info("[CreateWallet] received create wallet request. Data: {}", req);
        final var walletId = createWalletUseCase.createWallet(req.getName(), req.getOwnerId());

        final var model = EntityModel.of(new GenericResponse<>(null, walletId));
        final var link = linkTo(methodOn(WalletController.class).get(walletId)).withRel("created");
        model.add(link);
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<EntityModel<GenericResponse<ViewWalletInfoDto>>> get(@PathVariable UUID walletId) {
        log.info("[ViewWallet] received view wallet request. Data: {}", walletId);
        final var dto = viewWalletInfoUseCase.walletInfo(walletId);

        final var model = EntityModel.of(new GenericResponse<>(null, dto));
        final var link = linkTo(methodOn(WalletController.class).get(walletId)).withSelfRel();
        model.add(link);
        log.info("[ViewWallet] returning wallet: {}.", walletId);
        return ResponseEntity.ok(model);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<EntityModel<GenericResponse<ViewWalletInfoDto>>> deposit(
            @PathVariable UUID walletId,
            @RequestBody DepositRequest req) {
        log.info("[Deposit] received deposit request to wallet: {}. Data: {}", walletId, req);
        final var dto = makeDepositUseCase.makeDeposit(walletId, req.getAmount());

        final var model = EntityModel.of(new GenericResponse<>(null, dto));
        final var link = linkTo(methodOn(WalletController.class).get(walletId)).withSelfRel();
        model.add(link);
        return ResponseEntity.ok(model);
    }
}
