
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.application.ViewTransferUseCase;
import com.pedrogonzalezj.banking.application.WireTransferUseCase;
import com.pedrogonzalezj.banking.application.dto.ViewTransferDto;
import com.pedrogonzalezj.banking.domain.transfer.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
public class TransferControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WireTransferUseCase wireTransferUseCase;
    @MockBean
    private ViewTransferUseCase viewTransferUseCase;

    @Test
    public void wireTransfer_createsTransferAndReturnsTransferId() throws Exception {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's transfer";
        final var amount = 100L;
        final var link = linkTo(methodOn(TransferController.class).get(transferId)).toString();
        final var requestBody = """
                {
                    "source": "%s",
                    "destination": "%s",
                    "message": "%s",
                    "amount": %s
                  }
                """.formatted(sourceId, destinationId, message, amount);

        when(wireTransferUseCase.wireTransfer(any(), any(), any(), any())).thenReturn(transferId);

        mockMvc.perform(post("/transfers").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(transferId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.created.href").value(link));

        verify(wireTransferUseCase).wireTransfer(sourceId, destinationId, message, amount);
    }

    @Test
    public void get_returnsViewTransferDto() throws Exception {
        final var transferId = UUID.randomUUID();
        final var sourceId = UUID.randomUUID();
        final var destinationId = UUID.randomUUID();
        final var message = "John Doe's transfer";
        final var amount = 100L;
        final var link = linkTo(methodOn(TransferController.class).get(transferId)).toString();
        final var dto = ViewTransferDto.builder()
                .id(transferId)
                .source(sourceId)
                .destination(destinationId)
                .message(message)
                .amount(amount)
                .status(Status.FINISHED.name())
                .build();

        when(viewTransferUseCase.transferInfo(any())).thenReturn(dto);

        mockMvc.perform(get("/transfers/{transferId}", transferId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(transferId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.source").value(sourceId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.destination").value(destinationId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.message").value(message))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.amount").value(amount))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value(dto.getStatus()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.self.href").value(link));

        verify(viewTransferUseCase).transferInfo(transferId);
    }
}
