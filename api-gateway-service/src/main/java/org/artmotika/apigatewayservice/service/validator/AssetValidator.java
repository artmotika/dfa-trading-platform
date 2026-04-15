package org.artmotika.apigatewayservice.service.validator;

import lombok.RequiredArgsConstructor;
import org.artmotika.apigatewayservice.exception.AmlViolationException;
import org.artmotika.apigatewayservice.model.Asset;
import org.artmotika.apigatewayservice.model.User;
import org.artmotika.apigatewayservice.repo.AssetRepository;
import org.artmotika.common.dto.AssetStatus;
import org.artmotika.common.dto.OrderRequestDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetValidator implements OrderValidator {
    private final AssetRepository assetRepository;

    @Override
    public void validate(OrderRequestDto order, User user) {
        Asset asset = assetRepository.findById(order.getAssetId())
                .orElseThrow(() -> new AmlViolationException("Asset not found"));

        if (asset.getStatus() == AssetStatus.IPO_PLANNED) {
            throw new AmlViolationException("Asset is not yet available for purchase (IPO Planned)");
        }

        if (asset.getStatus() == AssetStatus.SUSPENDED) {
            throw new AmlViolationException("Trading is suspended for this asset");
        }

        if (asset.getStatus() == AssetStatus.IPO_ACTIVE) {
            // During IPO, only buy orders at IPO price are allowed
            if (order.getType().name().equals("SELL")) {
                throw new AmlViolationException("Selling is not allowed during IPO phase");
            }
            if (order.getPrice().compareTo(asset.getIpoPrice()) != 0) {
                throw new AmlViolationException("Orders during IPO must be at the fixed IPO price: " + asset.getIpoPrice());
            }
        }
    }
}
