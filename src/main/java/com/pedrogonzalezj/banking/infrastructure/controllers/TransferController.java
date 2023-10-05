
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.application.ViewTransferUseCase;
import com.pedrogonzalezj.banking.application.WireTransferUseCase;
import com.pedrogonzalezj.banking.application.dto.ViewTransferDto;
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
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final WireTransferUseCase wireTransferUseCase;
    private final ViewTransferUseCase viewTransferUseCase;

    @PostMapping
    public ResponseEntity<EntityModel<GenericResponse<UUID>>> wireTransfer(@RequestBody WireTransferRequest req) {
        log.info("[WireTransfer] received wire transfer request. Data: {}", req);
        final var transferId = wireTransferUseCase.wireTransfer(
                req.getSource(),
                req.getDestination(),
                req.getMessage(),
                req.getAmount()
        );

        final var model = EntityModel.of(new GenericResponse<>(null, transferId));
        final var link = linkTo(methodOn(TransferController.class).get(transferId)).withRel("created");
        model.add(link);
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<EntityModel<GenericResponse<ViewTransferDto>>> get(@PathVariable UUID transferId) {
        log.info("[ViewTransfer] received view transfer request. Data: {}", transferId);
        final var dto = viewTransferUseCase.transferInfo(transferId);

        final var model = EntityModel.of(new GenericResponse<>(null, dto));
        final var link = linkTo(methodOn(TransferController.class).get(transferId)).withSelfRel();
        model.add(link);
        return ResponseEntity.ok(model);
    }
}
