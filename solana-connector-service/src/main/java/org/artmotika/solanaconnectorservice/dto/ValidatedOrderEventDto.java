package org.artmotika.solanaconnectorservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ValidatedOrderEventDto {
    private String id;
    private String assetId;
    private String sellerWallet;
    private String buyerWallet;
    private String sellerTokenAccount;
    private String buyerTokenAccount;
    private BigDecimal amount;
    private BigDecimal price;
}
