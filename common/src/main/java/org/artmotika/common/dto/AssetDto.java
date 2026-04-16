package org.artmotika.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDto {
    private String id;
    private String solanaMintAddress;
    private String name;
    private Long totalSupply;
    private AssetType type;
    private AssetStatus status;
    private BigDecimal ipoPrice;
    private String legalDocHash;
    private Long tradeUnlockTimestamp;
}
