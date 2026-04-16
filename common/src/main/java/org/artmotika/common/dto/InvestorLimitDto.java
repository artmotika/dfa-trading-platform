package org.artmotika.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorLimitDto {
    private String userId;
    private BigDecimal annualInvestment;
    private LocalDateTime lastReset;
}
