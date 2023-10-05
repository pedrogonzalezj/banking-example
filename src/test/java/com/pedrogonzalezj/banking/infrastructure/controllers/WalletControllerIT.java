
package com.pedrogonzalezj.banking.infrastructure.controllers;

import com.pedrogonzalezj.banking.application.CreateWalletUseCase;
import com.pedrogonzalezj.banking.application.MakeDepositUseCase;
import com.pedrogonzalezj.banking.application.ViewWalletInfoUseCase;
import com.pedrogonzalezj.banking.application.dto.ViewWalletInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
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
public class WalletControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CreateWalletUseCase createWalletUseCase;
    @MockBean
    private ViewWalletInfoUseCase viewWalletInfoUseCase;
    @MockBean
    private MakeDepositUseCase makeDepositUseCase;


    @Test
    public void create_returnsWalletId() throws Exception {
        final var walletId = UUID.randomUUID();
        final var name = "John Doe's wallet";
        final var ownerId = UUID.randomUUID();
        final var link = linkTo(methodOn(WalletController.class).get(walletId)).toString();
        final var requestBody = """
                {
                   "name": "%s",
                   "ownerId": "%s"
                 }
                """.formatted(name, ownerId.toString());
        when(createWalletUseCase.createWallet(any(), any())).thenReturn(walletId);

        mockMvc.perform(post("/wallets").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(walletId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.created.href").value(link));

        verify(createWalletUseCase).createWallet(name, ownerId);
    }

    @Test
    public void deposit_makesDepositAndReturnsWalletInfo() throws Exception {
        final var walletId = UUID.randomUUID();
        final var dto = ViewWalletInfoDto.builder()
                .id(walletId)
                .ownerId(UUID.randomUUID())
                .name("John Doe's wallet")
                .money(BigInteger.TEN)
                .build();
        final var link = linkTo(methodOn(WalletController.class).get(walletId)).toString();
        final var amount = 10L;
        final var requestBody = """
                {
                    "amount": %s
                  }
                """.formatted(amount);

        when(makeDepositUseCase.makeDeposit(any(), any())).thenReturn(dto);

        mockMvc.perform(post("/wallets/{walletId}/deposit", walletId).content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(walletId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value(dto.getName()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.ownerId").value(dto.getOwnerId().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.money").value(dto.getMoney().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.self.href").value(link));

        verify(makeDepositUseCase).makeDeposit(walletId, amount);
    }

    @Test
    public void get_returnsWalletInfo() throws Exception {
        final var walletId = UUID.randomUUID();
        final var dto = ViewWalletInfoDto.builder()
                .id(walletId)
                .ownerId(UUID.randomUUID())
                .name("John Doe's wallet")
                .money(BigInteger.TEN)
                .build();
        final var link = linkTo(methodOn(WalletController.class).get(walletId)).toString();

        when(viewWalletInfoUseCase.walletInfo(any())).thenReturn(dto);

        mockMvc.perform(get("/wallets/{walletId}", walletId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(walletId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value(dto.getName()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.ownerId").value(dto.getOwnerId().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.money").value(dto.getMoney().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.error").isEmpty())
                .andExpect(status().isOk()).andExpect(jsonPath("$._links.self.href").value(link));

        verify(viewWalletInfoUseCase).walletInfo(walletId);
    }
}
