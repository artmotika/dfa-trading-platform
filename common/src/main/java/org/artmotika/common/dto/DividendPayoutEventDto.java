package org.artmotika.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DividendPayoutEventDto {
    private String userId;
    private String userWallet;
    private String assetId;
    private String mintAddress;
    private long amount;
    private String actionId;
    private String sourceTokenAccount;
}
