package org.artmotika.tradingengineservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "tax_ledger")
@Data
public class TaxLedger {
    @Id private String id;
    private String userId; // Logical reference to auth-service
    @OneToOne @JoinColumn(name = "order_id") private Order order;
    private BigDecimal taxAmount;
    private LocalDateTime timestamp;
}
