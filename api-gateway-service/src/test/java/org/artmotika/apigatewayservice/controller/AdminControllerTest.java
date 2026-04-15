package org.artmotika.apigatewayservice.controller;

import org.artmotika.apigatewayservice.model.Asset;
import org.artmotika.apigatewayservice.repo.AssetRepository;
import org.artmotika.common.dto.AssetStatus;
import org.artmotika.common.dto.KycStatus;
import org.artmotika.apigatewayservice.model.User;
import org.artmotika.apigatewayservice.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock private UserRepository userRepository;
    @Mock private AssetRepository assetRepository;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AdminController adminController;

    @Test
    void createAsset_ShouldSaveAndSendEvent() {
        Map<String, Object> req = Map.of(
                "name", "Test Asset",
                "totalSupply", 1000000L,
                "type", "EQUITY",
                "ipoPrice", "10.50",
                "legalDocHash", "HASH123",
                "tradeUnlockTimestamp", 1700000000L
        );

        ResponseEntity<Asset> response = adminController.createAsset(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Asset", response.getBody().getName());
        assertEquals("HASH123", response.getBody().getLegalDocHash());
        verify(assetRepository, times(1)).save(any());
        verify(kafkaTemplate, times(1)).send(eq("assets.created"), any());
    }

    @Test
    void startIpo_ShouldUpdateStatusAndSendEvent() {
        Asset asset = new Asset();
        asset.setId("a1");
        when(assetRepository.findById("a1")).thenReturn(Optional.of(asset));

        ResponseEntity<String> response = adminController.startIpo("a1");

        assertEquals("IPO started", response.getBody());
        assertEquals(AssetStatus.IPO_ACTIVE, asset.getStatus());
        verify(assetRepository, times(1)).save(asset);
        verify(kafkaTemplate, times(1)).send(eq("ipo.status"), any());
    }

    @Test
    void finalizeIpo_ShouldUpdateStatusAndSendEvent() {
        Asset asset = new Asset();
        asset.setId("a1");
        when(assetRepository.findById("a1")).thenReturn(Optional.of(asset));

        ResponseEntity<String> response = adminController.finalizeIpo("a1");

        assertEquals("IPO finalized, trading enabled", response.getBody());
        assertEquals(AssetStatus.TRADING, asset.getStatus());
        verify(assetRepository, times(1)).save(asset);
        verify(kafkaTemplate, times(1)).send(eq("ipo.status"), any());
    }

    @Test
    void startVote_ShouldSendEvent() {
        Map<String, Object> req = Map.of("assetId", "a1", "title", "Split?");
        ResponseEntity<String> response = adminController.startVote(req);
        assertEquals("Voting initiated", response.getBody());
        verify(kafkaTemplate, times(1)).send(eq("vote.started"), any(Map.class));
    }

    @Test
    void updateKyc_ShouldSaveAndSendEvent() {
        User user = new User();
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        ResponseEntity<String> response = adminController.updateKyc(Map.of("userId", "u1", "approved", true));

        assertEquals("KYC Updated", response.getBody());
        assertEquals(KycStatus.APPROVED, user.getKycStatus());
        verify(userRepository, times(1)).save(user);
        verify(kafkaTemplate, times(1)).send(eq("kyc.updated"), any());
    }

    @Test
    void freeze_ShouldSendEvent() {
        ResponseEntity<String> response = adminController.freeze(Map.of("userId", "u1", "freeze", true));
        assertEquals("Freeze Command Sent", response.getBody());
        verify(kafkaTemplate, times(1)).send(eq("aml.frozen"), any());
    }

    @Test
    void clawback_ShouldSendEvent() {
        ResponseEntity<String> response = adminController.clawback(Map.of("target", "t1"));
        assertEquals("Clawback Command Sent", response.getBody());
        verify(kafkaTemplate, times(1)).send(eq("admin.clawback"), any());
    }
}
