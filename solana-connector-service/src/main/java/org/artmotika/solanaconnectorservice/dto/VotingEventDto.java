package org.artmotika.solanaconnectorservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class VotingEventDto {
    private String actionId;
    private String assetId;
    private String title;
    private List<String> options;
}
