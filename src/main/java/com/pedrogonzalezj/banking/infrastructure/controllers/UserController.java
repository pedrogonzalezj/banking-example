
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.application.RegisterUserUseCase;
import com.pedrogonzalezj.banking.application.ViewUserInfoUseCase;
import com.pedrogonzalezj.banking.application.command.RegisterUserCommand;
import com.pedrogonzalezj.banking.application.dto.ViewUserDto;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final ViewUserInfoUseCase viewUserInfoUseCase;

    @PostMapping
    public ResponseEntity<EntityModel<GenericResponse<UUID>>> register(@RequestBody RegisterUserRequest req) {
        log.info("[Register] received register user request. Data: {}", req);
        final var command = RegisterUserCommand.builder()
                .name(req.getName())
                .surname(req.getSurname())
                .nationalId(req.getNationalId())
                .birthDate(req.getBirthDate())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .street(req.getStreet())
                .number(req.getNumber())
                .apartmentNumber(req.getApartmentNumber())
                .zipCode(req.getZipCode())
                .city(req.getCity())
                .state(req.getState())
                .country(req.getCountry())
                .build();
        final var userId = registerUserUseCase.registerUser(command);

        final var model = EntityModel.of(new GenericResponse<>(null, userId));
        final var link = linkTo(methodOn(UserController.class).get(userId)).withRel("created");
        model.add(link);
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EntityModel<GenericResponse<ViewUserDto>>> get(@PathVariable UUID userId) {
        log.info("[ViewUser] received view user info request. Data: {}", userId);
        final var dto = viewUserInfoUseCase.userInfo(userId);

        final var model = EntityModel.of(new GenericResponse<>(null, dto));
        final var link = linkTo(methodOn(UserController.class).get(userId)).withSelfRel();
        model.add(link);
        return ResponseEntity.ok(model);
    }
}
