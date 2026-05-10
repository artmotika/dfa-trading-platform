package org.artmotika.tradingengineservice.controller;

import lombok.RequiredArgsConstructor;
import org.artmotika.common.dto.AssetDto;
import org.artmotika.common.dto.InvestorLimitDto;
import org.artmotika.tradingengineservice.mapper.AssetMapper;
import org.artmotika.tradingengineservice.mapper.InvestorLimitMapper;
import org.artmotika.tradingengineservice.model.Asset;
import org.artmotika.tradingengineservice.repo.AssetRepository;
import org.artmotika.tradingengineservice.repo.InvestorLimitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradingApiController {
    private final AssetRepository assetRepository;
    private final InvestorLimitRepository investorLimitRepository;
    private final AssetMapper assetMapper;
    private final InvestorLimitMapper investorLimitMapper;

    @GetMapping("/assets/{id}")
    public ResponseEntity<AssetDto> getAsset(@PathVariable String id) {
        Optional<Asset> assetOpt = assetRepository.findById(id);
        if (assetOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assetMapper.toDto(assetOpt.get()));
    }

    @GetMapping("/limits/{userId}")
    public ResponseEntity<InvestorLimitDto> getLimit(@PathVariable String userId) {
        return investorLimitRepository.findById(userId)
                .map(investorLimitMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(InvestorLimitDto.builder()
                        .userId(userId)
                        .annualInvestment(BigDecimal.ZERO)
                        .lastReset(LocalDateTime.now())
                        .build()));
    }
}
