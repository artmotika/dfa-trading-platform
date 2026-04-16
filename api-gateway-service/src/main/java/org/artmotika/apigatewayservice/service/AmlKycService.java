package org.artmotika.apigatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.artmotika.apigatewayservice.exception.KycNotVerifiedException;
import org.artmotika.common.dto.UserDto;
import org.artmotika.apigatewayservice.service.validator.OrderValidator;
import org.artmotika.common.dto.OrderRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmlKycService {
    private final KafkaTemplate<String, OrderRequestDto> kafkaTemplate;
    private final List<OrderValidator> validators;
    private final RestTemplate restTemplate;

    @Value("${app.services.auth}")
    private String authServiceUrl;

    public void processOrder(OrderRequestDto order) {
        UserDto user = restTemplate.getForObject(authServiceUrl + "/api/auth/users/" + order.getUserId(), UserDto.class);
        
        if (user == null) {
            throw new KycNotVerifiedException("User not found via Auth Service");
        }

        validators.forEach(v -> v.validate(order, user));

        kafkaTemplate.send("orders.created", order);
    }
}
