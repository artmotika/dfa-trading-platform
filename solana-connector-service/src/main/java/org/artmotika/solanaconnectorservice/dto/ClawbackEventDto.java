package org.artmotika.solanaconnectorservice.dto;

import lombok.Data;

@Data
public class ClawbackEventDto {
    private String assetId;
    private String targetWallet;
    private String destinationWallet;
    private String targetTokenAccount;
    private String destinationTokenAccount;
    private long amount;
    private String reason;
}
