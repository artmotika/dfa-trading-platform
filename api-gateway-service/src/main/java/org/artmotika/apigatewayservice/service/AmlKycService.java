package org.artmotika.apigatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.artmotika.common.dto.OrderRequestDto;
import org.artmotika.apigatewayservice.exception.AmlViolationException;
import org.artmotika.apigatewayservice.exception.KycNotVerifiedException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AmlKycService {
    private final KafkaTemplate<String, OrderRequestDto> kafkaTemplate;
    private final Map<String, List<Long>> userOrderTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Integer> userAmlScores = new ConcurrentHashMap<>();
    private final Map<String, String> mockUserKycDb = Map.of("user-1", "APPROVED", "user-2", "PENDING"); // Mock DB

    public void processOrder(OrderRequestDto order) {
        if (!"APPROVED".equals(mockUserKycDb.getOrDefault(order.getUserId(), "PENDING"))) {
            throw new KycNotVerifiedException("User KYC not approved");
        }

        long now = Instant.now().toEpochMilli();
        userOrderTimestamps.putIfAbsent(order.getUserId(), new ArrayList<>());
        List<Long> times = userOrderTimestamps.get(order.getUserId());
        times.removeIf(t -> now - t > 60000);
        times.add(now);

        if (times.size() > 5 || order.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
            userAmlScores.put(order.getUserId(), userAmlScores.getOrDefault(order.getUserId(), 0) + 50);
            throw new AmlViolationException("AML Policy Violation: Volume/Frequency limit exceeded");
        }

        kafkaTemplate.send("orders.created", order);
    }
}
