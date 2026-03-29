package org.artmotika.solanaconnectorservice.dto;

import lombok.Data;

@Data
public class ValidatedOrderEventDto {
    String id;
    String userId;
    String assetId;
}
