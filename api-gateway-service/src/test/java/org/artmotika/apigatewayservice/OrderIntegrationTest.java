package org.artmotika.apigatewayservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.artmotika.apigatewayservice.service.StateCacheService;
import org.artmotika.common.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaTemplate<String, OrderRequestDto> kafkaTemplate;

    @MockBean
    private StateCacheService stateCacheService;

    @MockBean
    private java.util.List<org.artmotika.apigatewayservice.service.validator.OrderValidator> validators;

    @Test
    void testFullOrderSubmissionFlow() throws Exception {
        // 1. Prepare User Context (as if JWT was parsed)
        UserDto user = UserDto.builder()
                .id("u1")
                .walletAddress("wallet123")
                .kycStatus(KycStatus.APPROVED)
                .amlRiskScore(0)
                .qualified(true)
                .frozen(false)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList())
        );

        // Mock Redis Cache Response for AssetValidator (even if validators is a mock, good to have it ready)
        AssetDto asset = new AssetDto();
        asset.setId("a1");
        asset.setStatus(AssetStatus.TRADING);
        when(stateCacheService.getAsset("a1")).thenReturn(asset);

        // 2. Prepare Order Request
        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setAssetId("a1");
        orderRequest.setType(OrderType.BUY);
        orderRequest.setAmount(new BigDecimal("10"));
        orderRequest.setPrice(new BigDecimal("100"));

        // 3. Execute POST /api/v1/orders
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isAccepted());

        // 4. Verify Kafka Event
        verify(kafkaTemplate).send(eq("orders.created"), argThat(o -> 
            o.getUserId().equals("u1") && 
            o.getWalletAddress().equals("wallet123")
        ));
    }
}
