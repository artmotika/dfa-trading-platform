package org.artmotika.tradingengineservice.dto;

import lombok.Data;
import org.artmotika.common.dto.AssetStatus;
import org.artmotika.common.dto.AssetType;
import java.math.BigDecimal;

@Data
public class AssetCreatedEventDto {
    private String id;
    private String solanaMintAddress;
    private String name;
    private long totalSupply;
    private AssetType type;
    private AssetStatus status;
    private BigDecimal ipoPrice;
}
