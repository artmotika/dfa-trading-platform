package org.artmotika.apigatewayservice.service.validator;

import org.artmotika.common.dto.UserDto;
import org.artmotika.common.dto.OrderRequestDto;

public interface OrderValidator {
    void validate(OrderRequestDto order, UserDto user);
}
