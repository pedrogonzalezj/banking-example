
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.domain.transfer.TransferNotFoundException;
import com.pedrogonzalezj.banking.domain.user.UserNotFoundException;
import com.pedrogonzalezj.banking.domain.wallet.WalletNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class BankingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { TransferNotFoundException.class })
    protected ResponseEntity<GenericResponse<String>> handleTransferNotFoundException(TransferNotFoundException ex, WebRequest request) {
        log.error("[error] user request {} contains wrong data: Transfer: {} does not exists.\n<{}>", request, ex.getTransferId(), ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.ENTITY_NOT_FOUND.name());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { UserNotFoundException.class })
    protected ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("[error] user request {} contains wrong data: User: {} does not exists.\n<{}>", request, ex.getUserId(), ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.ENTITY_NOT_FOUND.name());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { WalletNotFoundException.class })
    protected ResponseEntity<Object> handleWalletNotFoundException(WalletNotFoundException ex, WebRequest request) {
        log.error("[error] user request {} contains wrong data: Wallet: {} does not exists.\n<{}>", request, ex.getWalletId(), ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.ENTITY_NOT_FOUND.name());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("[error] user request {} contains some wrong data, or some required data is not present.\n<{}>", request, ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.BAD_PARAMS.name());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(error, null));
    }

    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("[error] something failed while processing user request: {}.\n<{}>", request, ex.getMessage());
        final var error = new GenericResponse.Error(ex.getMessage(), ErrorTypes.SERVICE_ERROR.name());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(error, null));
    }

    public enum ErrorTypes {
        ENTITY_NOT_FOUND,
        BAD_PARAMS,
        SERVICE_ERROR
    }
}
