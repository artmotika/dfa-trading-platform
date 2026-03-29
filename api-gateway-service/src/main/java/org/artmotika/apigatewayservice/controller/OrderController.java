package org.artmotika.apigatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.artmotika.apigatewayservice.service.AmlKycService;
import org.artmotika.common.dto.OrderRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class OrderController {
    private final AmlKycService amlKycService;

    @PostMapping("/orders")
    public String submitOrder(@RequestBody OrderRequestDto order) {
        amlKycService.processOrder(order);
        return "Order Accepted";
    }
}