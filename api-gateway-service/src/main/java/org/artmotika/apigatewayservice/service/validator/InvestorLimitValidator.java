package org.artmotika.apigatewayservice.service.validator;

import lombok.RequiredArgsConstructor;
import org.artmotika.apigatewayservice.exception.AmlViolationException;
import org.artmotika.common.dto.InvestorLimitDto;
import org.artmotika.common.dto.UserDto;
import org.artmotika.common.dto.OrderRequestDto;
import org.artmotika.common.dto.OrderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InvestorLimitValidator implements OrderValidator {
    private final RestTemplate restTemplate;
    private static final BigDecimal RETAIL_ANNUAL_LIMIT = new BigDecimal("600000");

    @Value("${app.services.trading}")
    private String tradingServiceUrl;

    @Override
    public void validate(OrderRequestDto order, UserDto user) {
        if (!user.isQualified() && order.getType() == OrderType.BUY) {
            BigDecimal orderValue = order.getAmount().multiply(order.getPrice());
            
            InvestorLimitDto limit = restTemplate.getForObject(tradingServiceUrl + "/api/limits/" + user.getId(), InvestorLimitDto.class);

            BigDecimal currentInvestment = (limit != null) ? limit.getAnnualInvestment() : BigDecimal.ZERO;
            BigDecimal newTotal = currentInvestment.add(orderValue);
            
            if (newTotal.compareTo(RETAIL_ANNUAL_LIMIT) > 0) {
                throw new AmlViolationException("Retail investor annual limit (600,000 RUB) exceeded");
            }
        }
    }
}
