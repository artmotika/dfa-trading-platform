package org.artmotika.apigatewayservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.artmotika.common.dto.AssetStatus;
import org.artmotika.common.dto.AssetType;

import java.math.BigDecimal;

@Data @Entity @Table(name = "assets")
public class Asset {
    @Id private String id;
    private String solanaMintAddress;
    private String name;
    private long totalSupply;
    @Enumerated(EnumType.STRING)
    private AssetType type;
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
    private BigDecimal ipoPrice;
    private String legalDocHash;
    private long tradeUnlockTimestamp;
}
