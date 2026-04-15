package org.artmotika.solanaconnectorservice.dto;

import lombok.Data;

@Data
public class KycUpdateEventDto {
    private String userId;
    private String assetId;
    private String userWallet;
    private boolean approved;
}
