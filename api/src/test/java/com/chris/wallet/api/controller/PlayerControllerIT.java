package com.chris.wallet.api.controller;

import com.chris.wallet.api.contract.PlayerApi;
import com.chris.wallet.api.contract.PlayerRequestApi;
import com.chris.wallet.api.contract.PlayersApi;
import com.chris.wallet.api.contract.WalletApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class PlayerControllerIT {

    @Autowired
    protected MockMvc mockMvc;

    final ObjectMapper mapper = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


    @Test
    @Order(1)
    public void create_player_should_be_created_with_http_201() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("cikku@gmail.com")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andReturn();
    }

    @Test
    @Order(2)
    public void create_already_exist_player_should_fail_with_http_400() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("cikku@gmail.com")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(3)
    public void create_player_with_missing_name_should_fail_with_http_400() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .surname("test")
                                               .username("cikku@gmail.com")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(4)
    public void create_player_with_missing_surname_should_fail_with_http_400() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .username("cikku@gmail.com")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(5)
    public void create_player_with_missing_username_should_fail_with_http_400() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(6)
    public void create_player_with_invalid_username_should_fail_with_http_400() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("cikku")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }

    @Test
    @Order(7)
    public void edit_player_should_be_updated_with_http_201() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("cikku_updated@gmail.com")
                                               .build();


        final MvcResult resultAllPlayers = mockMvc.perform(MockMvcRequestBuilders.get("/player/all"))
                                                  .andExpect(status().isOk())
                                                  .andReturn();
        final WalletApiResponse<PlayersApi> playerApiWalletApiResponse = mapToResponse(resultAllPlayers, new TypeReference<>() {});
        final PlayerApi existingPlayerApi = playerApiWalletApiResponse.getData().getPlayers().stream().findFirst().orElse(null);
        assert existingPlayerApi != null;
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/player/" + existingPlayerApi.getId())
                                                                       .content(mapper.writeValueAsString(playerRequestApi))
                                                                       .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isAccepted())
                                        .andReturn();
        final WalletApiResponse<PlayerApi> updatedPlayerApi = mapToResponse(result, new TypeReference<>() {});
        Assertions.assertEquals(updatedPlayerApi.getData().getUsername(), "cikku_updated@gmail.com");

    }


    @Test
    @Order(8)
    public void edit_invalid_player_should_not_be_updated_with_http_400() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("cikku_updated@gmail.com")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.put("/player/" + UUID.randomUUID().toString())
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andReturn();
    }


    @Test
    @Order(9)
    public void get_player_should_be_successful_with_http_200() throws Exception {


        final MvcResult resultAllPlayers = mockMvc.perform(MockMvcRequestBuilders.get("/player/all"))
                                                  .andExpect(status().isOk())
                                                  .andReturn();
        final WalletApiResponse<PlayersApi> playerApiWalletApiResponse = mapToResponse(resultAllPlayers, new TypeReference<>() {});
        final PlayerApi existingPlayerApi = playerApiWalletApiResponse.getData().getPlayers().stream().findFirst().orElse(null);
        assert existingPlayerApi != null;
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/player")
                                                                       .param("playerId", existingPlayerApi.getId().toString()))
                                        .andExpect(status().isOk())
                                        .andReturn();
        final WalletApiResponse<PlayerApi> player = mapToResponse(result, new TypeReference<>() {});
        Assertions.assertEquals(player.getData().getUsername(), "cikku_updated@gmail.com");

    }


    @Test
    @Order(10)
    public void get_invalid_player_should_fail_with_http_400() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/player")
                                              .param("playerId", UUID.randomUUID().toString()))
               .andExpect(status().isBadRequest())
               .andReturn();

    }


    @Test
    @Order(11)
    public void get_all_players_should_be_successful_with_http_200() throws Exception {

        val playerRequestApi = PlayerRequestApi.builder()
                                               .name("test")
                                               .surname("test")
                                               .username("cikku@gmail.com")
                                               .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/player")
                                              .content(mapper.writeValueAsString(playerRequestApi))
                                              .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andReturn();

        final MvcResult resultAllPlayers = mockMvc.perform(MockMvcRequestBuilders.get("/player/all"))
                                                  .andExpect(status().isOk())
                                                  .andReturn();
        final WalletApiResponse<PlayersApi> playerApiWalletApiResponse = mapToResponse(resultAllPlayers, new TypeReference<>() {});

        Assertions.assertNotNull(playerApiWalletApiResponse);
        Assertions.assertEquals(playerApiWalletApiResponse.getData().getPlayers().size(), 2);
    }


    @SneakyThrows
    private <T> T mapToResponse(final MvcResult result, final TypeReference<T> typeReference) {
        return mapper.readValue(result.getResponse().getContentAsString(), typeReference);
    }

}
