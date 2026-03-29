package org.artmotika.tradingengineservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "orders")
public class Order {
    @Id private String id;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    @ManyToOne @JoinColumn(name = "asset_id") private Asset asset;
    @Enumerated(EnumType.STRING) private OrderType type;
    private BigDecimal amount;
    private BigDecimal price;
    @Enumerated(EnumType.STRING) private OrderStatus status;
    private LocalDateTime createdAt;
    public enum OrderType { BUY, SELL }
    public enum OrderStatus { PENDING, EXECUTING, COMPLETED, FAILED }
}