package org.artmotika.solanaconnectorservice.service;

import org.artmotika.solanaconnectorservice.dto.VotingEventDto;
import org.artmotika.solanaconnectorservice.dto.ValidatedOrderEventDto;
import org.artmotika.solanaconnectorservice.dto.ExecutionResultDto;
import org.artmotika.solanaconnectorservice.dto.KycUpdateEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolanaBlockchainServiceTest {

    private static final String VALID_PUBKEY = "vines1vzrYbzduYv9bP5McaS1quZ756C87S9ER69s9P";

    @Mock
    private KafkaTemplate<String, ExecutionResultDto> kafkaTemplate;

    @InjectMocks
    private SolanaBlockchainService solanaBlockchainService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(solanaBlockchainService, "rpcUrl", "https://api.devnet.solana.com");
        ReflectionTestUtils.setField(solanaBlockchainService, "programIdStr", "Dfa1111111111111111111111111111111111111111");
        solanaBlockchainService.init();
    }

    @Test
    void createAssetOnChain_ShouldNotCrash() {
        Map<String, Object> asset = Map.of(
            "id", "a1",
            "name", "Test Asset",
            "totalSupply", 1000L,
            "solanaMintAddress", VALID_PUBKEY
        );
        solanaBlockchainService.createAssetOnChain(asset);
    }

    @Test
    void toggleIpoOnChain_ShouldNotCrash() {
        solanaBlockchainService.toggleIpoOnChain(Map.of("assetId", "a1", "status", "IPO_ACTIVE"));
    }

    @Test
    void startVotingOnChain_ShouldNotCrash() {
        VotingEventDto event = new VotingEventDto();
        event.setActionId("v1");
        event.setAssetId("a1");
        event.setTitle("Test Vote");
        event.setOptions(List.of("A", "B"));
        solanaBlockchainService.startVotingOnChain(event);
    }

    @Test
    void updateKycOnChain_ShouldNotCrash() {
        KycUpdateEventDto event = new KycUpdateEventDto();
        event.setAssetId("a1");
        event.setUserWallet(VALID_PUBKEY);
        event.setApproved(true);
        solanaBlockchainService.updateKycOnChain(event);
    }

    @Test
    void executeDividendPayout_ShouldNotCrash() {
        Map<String, Object> event = Map.of(
            "sourceTokenAccount", VALID_PUBKEY,
            "userTokenAccount", VALID_PUBKEY,
            "amount", 100L
        );
        solanaBlockchainService.executeDividendPayout(event);
    }
}
