package org.artmotika.apigatewayservice.controller;

import lombok.RequiredArgsConstructor;
import org.artmotika.apigatewayservice.model.Asset;
import org.artmotika.apigatewayservice.model.User;
import org.artmotika.apigatewayservice.repo.AssetRepository;
import org.artmotika.apigatewayservice.repo.UserRepository;
import org.artmotika.common.dto.AssetStatus;
import org.artmotika.common.dto.AssetType;
import org.artmotika.common.dto.KycStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/assets")
    public ResponseEntity<Asset> createAsset(@RequestBody Map<String, Object> req) {
        Asset asset = new Asset();
        asset.setId(UUID.randomUUID().toString());
        asset.setName((String) req.get("name"));
        asset.setTotalSupply(((Number) req.get("totalSupply")).longValue());
        asset.setType(AssetType.valueOf((String) req.get("type")));
        asset.setStatus(AssetStatus.IPO_PLANNED);
        asset.setIpoPrice(new BigDecimal(req.get("ipoPrice").toString()));
        asset.setLegalDocHash((String) req.getOrDefault("legalDocHash", "MOCK_HASH"));
        asset.setTradeUnlockTimestamp(((Number) req.getOrDefault("tradeUnlockTimestamp", System.currentTimeMillis() / 1000 + 3600)).longValue());
        asset.setSolanaMintAddress("MOCK_MINT_" + UUID.randomUUID().toString().substring(0, 8));

        assetRepository.save(asset);
        kafkaTemplate.send("assets.created", asset);
        return ResponseEntity.ok(asset);
    }

    @PostMapping("/ipo/start")
    public ResponseEntity<String> startIpo(@RequestParam String assetId) {
        Asset asset = assetRepository.findById(assetId).orElseThrow();
        asset.setStatus(AssetStatus.IPO_ACTIVE);
        assetRepository.save(asset);
        kafkaTemplate.send("ipo.status", Map.of("assetId", assetId, "status", AssetStatus.IPO_ACTIVE));
        return ResponseEntity.ok("IPO started");
    }

    @PostMapping("/ipo/finalize")
    public ResponseEntity<String> finalizeIpo(@RequestParam String assetId) {
        Asset asset = assetRepository.findById(assetId).orElseThrow();
        asset.setStatus(AssetStatus.TRADING);
        assetRepository.save(asset);
        kafkaTemplate.send("ipo.status", Map.of("assetId", assetId, "status", AssetStatus.TRADING));
        return ResponseEntity.ok("IPO finalized, trading enabled");
    }

    @PostMapping("/vote")
    public ResponseEntity<String> startVote(@RequestBody Map<String, Object> req) {
        Map<String, Object> mutableReq = new java.util.HashMap<>(req);
        if (!mutableReq.containsKey("actionId")) {
            mutableReq.put("actionId", UUID.randomUUID().toString());
        }
        kafkaTemplate.send("vote.started", mutableReq);
        return ResponseEntity.ok("Voting initiated");
    }

    @PostMapping("/kyc")
    public ResponseEntity<String> updateKyc(@RequestBody Map<String, Object> req) {
        String userId = (String) req.get("userId");
        boolean approved = (Boolean) req.get("approved");
        
        User user = userRepository.findById(userId).orElseThrow();
        user.setKycStatus(approved ? KycStatus.APPROVED : KycStatus.REJECTED);
        userRepository.save(user);
        
        kafkaTemplate.send("kyc.updated", Map.of("userId", userId, "approved", approved));
        return ResponseEntity.ok("KYC Updated");
    }

    @PostMapping("/freeze")
    public ResponseEntity<String> freeze(@RequestBody Map<String, Object> req) {
        String userId = (String) req.get("userId");
        boolean freeze = (Boolean) req.get("freeze");
        
        kafkaTemplate.send("aml.frozen", Map.of("userId", userId, "freeze", freeze));
        return ResponseEntity.ok("Freeze Command Sent");
    }

    @PostMapping("/clawback")
    public ResponseEntity<String> clawback(@RequestBody Map<String, Object> req) {
        kafkaTemplate.send("admin.clawback", req);
        return ResponseEntity.ok("Clawback Command Sent");
    }
}
