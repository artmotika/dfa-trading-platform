package org.artmotika.solanaconnectorservice;

import org.artmotika.solanaconnectorservice.dto.ExecutionResultDto;
import org.artmotika.solanaconnectorservice.dto.ValidatedOrderEventDto;
import org.artmotika.solanaconnectorservice.service.SolanaBlockchainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class SolanaConnectorTests {
    @Autowired private SolanaBlockchainService service;
    @MockBean private KafkaTemplate<String, ExecutionResultDto> kafkaTemplate;

    @Test
    void testBlockchainExecution() throws InterruptedException {
        ValidatedOrderEventDto event = new ValidatedOrderEventDto();
        event.setId("order-1");
        service.executeOnChain(event);
        verify(kafkaTemplate).send(any(String.class), any(ExecutionResultDto.class));
    }
}
