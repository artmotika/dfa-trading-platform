package org.artmotika.apigatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.artmotika.apigatewayservice.exception.KycNotVerifiedException;
import org.artmotika.common.dto.UserDto;
import org.artmotika.apigatewayservice.service.validator.OrderValidator;
import org.artmotika.common.dto.OrderRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmlKycService {
    private final KafkaTemplate<String, OrderRequestDto> kafkaTemplate;
    private final List<OrderValidator> validators;
    private final RestTemplate restTemplate;

    @Value("${app.services.auth}")
    private String authServiceUrl;

    public void processOrder(OrderRequestDto order) {
        log.debug("Fetching user {} from {}", order.getUserId(), authServiceUrl);
        UserDto user = restTemplate.getForObject(authServiceUrl + "/api/v1/auth/users/" + order.getUserId(), UserDto.class);
        
        if (user == null) {
            log.error("User not found!");
            throw new KycNotVerifiedException("User not found via Auth Service");
        }

        log.debug("User found, wallet: {}", user.getWalletAddress());
        order.setWalletAddress(user.getWalletAddress());

        validators.forEach(v -> {
            log.debug("Running validator: {}", v.getClass().getSimpleName());
            try {
                v.validate(order, user);
            } catch (Exception e) {
                log.warn("Validator {} threw: {}", v.getClass().getSimpleName(), e.getMessage());
                throw e;
            }
        });

        log.info("Sending to Kafka: {}", order);
        kafkaTemplate.send("orders.created", order);
    }
}
