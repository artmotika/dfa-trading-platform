package org.artmotika.apigatewayservice.repo;

import org.artmotika.apigatewayservice.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, String> {
}
