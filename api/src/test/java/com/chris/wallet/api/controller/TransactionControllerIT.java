package com.chris.wallet.api.controller;

import com.chris.wallet.api.config.WalletConfig;
import com.chris.wallet.api.contract.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TransactionControllerIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WalletConfig walletConfig;

    final ObjectMapper mapper = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private UUID playerId;
    private final UUID transactionId = UUID.randomUUID();


    @SneakyThrows
    @BeforeAll
    public void init() {
        stubFor(get(urlPathEqualTo("/api/latest.json"))
                    .withQueryParam("app_id", equalTo(walletConfig.getAppId()))
                    .willReturn(aResponse()
                                    .withStatus(OK.value())
                                    .withHeader("Content-Type", "application/json")
                                    .withBodyFile("rates.json")));

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("transaction@gmail.com")
                                               .build();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                                                       .content(mapper.writeValueAsString(playerRequestApi))
                                                                       .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isCreated())
                                        .andReturn();

        final WalletApiResponse<PlayerApi> playerApi = mapToResponse(result, new TypeReference<>() {});
        playerId = playerApi.getData().getId();

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(transactionId)
                                                         .amount(BigDecimal.TEN)
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andReturn();


    }

    @Test
    @Order(1)
    public void add_transaction_should_be_created_with_http_201() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .amount(BigDecimal.TEN)
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andReturn();
    }

    @Test
    @Order(2)
    public void add_transaction_with_transaction_already_exist_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(transactionId)
                                                         .amount(BigDecimal.TEN)
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(3)
    public void add_transaction_with_not_enough_balance_should_fail_with_http_402() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .amount(BigDecimal.TEN.multiply(BigDecimal.TEN))
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.DEBIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isPaymentRequired())
               .andReturn();
    }

    @Test
    @Order(4)
    public void add_transaction_with_missing_transaction_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .amount(BigDecimal.TEN)
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(5)
    public void add_transaction_with_missing_amount_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .amount(BigDecimal.TEN)
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(6)
    public void add_transaction_with_missing_payment_direction_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .amount(BigDecimal.TEN)
                                                         .currency(Currency.getInstance("EUR"))
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(7)
    public void add_transaction_with_missing_player_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .amount(BigDecimal.TEN)
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(8)
    public void add_transaction_with_missing_currency_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(playerId)
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(9)
    public void add_transaction_with_invalid_player_should_fail_with_http_400() throws Exception {

        val transactionApiRequest = TransactionApiRequest.builder()
                                                         .id(UUID.randomUUID())
                                                         .currency(Currency.getInstance("EUR"))
                                                         .paymentDirection(PaymentDirection.CREDIT)
                                                         .playerId(UUID.randomUUID())
                                                         .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/wallet")
                                              .content(mapper.writeValueAsString(transactionApiRequest))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(9)
    public void get_player_balance_should_be_successful_with_http_200() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wallet/" + playerId + "/balance"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        final WalletApiResponse<PlayerBalanceApi> playerBalance = mapToResponse(result, new TypeReference<>() {});
        Assertions.assertEquals(playerBalance.getData().getAmount(), BigDecimal.valueOf(23.72));
        Assertions.assertEquals(playerBalance.getData().getCurrency(), Currency.getInstance("USD"));
    }

    @Test
    @Order(10)
    public void get_invalid_player_balance_should_be_successful_with_http_200() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wallet/" + UUID.randomUUID() + "/balance"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        final WalletApiResponse<PlayerBalanceApi> playerBalance = mapToResponse(result, new TypeReference<>() {});
        Assertions.assertEquals(playerBalance.getData().getAmount(), BigDecimal.ZERO);
        Assertions.assertEquals(playerBalance.getData().getCurrency(), Currency.getInstance("USD"));
    }

    @Test
    @Order(11)
    public void get_transactions_for_player_should_be_successful_with_http_200() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wallet/" + playerId + "/history"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        final WalletApiResponse<TransactionHistoryResponseApi> transactionHistory = mapToResponse(result, new TypeReference<>() {});
        Assertions.assertEquals(2, transactionHistory.getData().getTransactions().size() );
        Assertions.assertEquals(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_DOWN), transactionHistory.getData().getTransactions().stream().map(TransactionApi::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    @Order(12)
    public void get_transactions_for_invalid_player_should_be_successful_with_http_200() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/wallet/" + UUID.randomUUID() + "/history"))
                                        .andExpect(status().isOk())
                                        .andReturn();

        final WalletApiResponse<TransactionHistoryResponseApi> transactionHistory = mapToResponse(result, new TypeReference<>() {});
        Assertions.assertEquals(0, transactionHistory.getData().getTransactions().size() );
        Assertions.assertEquals(BigDecimal.valueOf(0), transactionHistory.getData().getTransactions().stream().map(TransactionApi::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }



    @SneakyThrows
    private <T> T mapToResponse(final MvcResult result, final TypeReference<T> typeReference) {
        return mapper.readValue(result.getResponse().getContentAsString(), typeReference);
    }
}
