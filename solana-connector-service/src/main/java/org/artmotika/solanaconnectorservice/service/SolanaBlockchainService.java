package org.artmotika.solanaconnectorservice.service;

import lombok.RequiredArgsConstructor;
import org.artmotika.solanaconnectorservice.dto.ExecutionResultDto;
import org.artmotika.solanaconnectorservice.dto.ValidatedOrderEventDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SolanaBlockchainService {
    private final WebClient webClient = WebClient.create("https://api.devnet.solana.com");
    private final KafkaTemplate<String, ExecutionResultDto> kafkaTemplate;

    @KafkaListener(topics = "orders.validated", groupId = "solana-connector-group")
    public void executeOnChain(ValidatedOrderEventDto event) throws InterruptedException {
        String dummyBase58Tx = "tx_" + UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> rpcPayload = new HashMap<>();
        rpcPayload.put("jsonrpc", "2.0");
        rpcPayload.put("id", 1);
        rpcPayload.put("method", "sendTransaction");
        rpcPayload.put("params", List.of(dummyBase58Tx));

        webClient.post().bodyValue(rpcPayload).retrieve().bodyToMono(String.class).block();

        // Polling mock
        boolean finalized = false;
        while (!finalized) {
            Map<String, Object> statusPayload = Map.of("jsonrpc", "2.0", "id", 1, "method", "getSignatureStatuses", "params", List.of(List.of(dummyBase58Tx)));
            String status = webClient.post().bodyValue(statusPayload).retrieve().bodyToMono(String.class).block();
            if (status != null && status.contains("finalized")) { finalized = true; }
            else { Thread.sleep(1000); finalized = true; } // Mocking finalized for loop break
        }

        ExecutionResultDto result = new ExecutionResultDto();
        result.setOrderId(event.getId());
        result.setTxHash(dummyBase58Tx);
        kafkaTemplate.send("trades.executed", result);
    }
}
