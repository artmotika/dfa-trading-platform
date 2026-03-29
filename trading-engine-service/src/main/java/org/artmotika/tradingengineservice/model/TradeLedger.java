package org.artmotika.tradingengineservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "trades_ledger")
public class TradeLedger {
    @Id private String id;
    @OneToOne @JoinColumn(name = "order_id") private Order order;
    private String transactionHash;
    private BigDecimal executionPrice;
    private LocalDateTime timestamp;
}