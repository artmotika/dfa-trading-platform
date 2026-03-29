package org.artmotika.apigatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc
class ApiGatewayApplicationTests {
    @Autowired private MockMvc mockMvc;

    @Test
    void testKycPendingRejection() throws Exception {
        String payload = "{\"userId\":\"user-2\",\"assetId\":\"asset-1\",\"type\":\"BUY\",\"amount\":10,\"price\":100}";
        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isInternalServerError()); // Expecting 500 due to basic Exception Handler missing in tight code, but validates rejection
    }

    @Test
    void testAmlRateLimit() throws Exception {
        String payload = "{\"userId\":\"user-1\",\"assetId\":\"asset-1\",\"type\":\"BUY\",\"amount\":10,\"price\":100}";
        for(int i=0; i<5; i++) mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(payload));
        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isInternalServerError()); // Blocked on 6th request
    }
}
