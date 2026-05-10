package org.artmotika.tradingengineservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investor_limits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorLimit {
    @Id
    private String userId;
    private BigDecimal annualInvestment;
    private LocalDateTime lastReset;
}
