package org.artmotika.tradingengineservice;

import org.artmotika.common.dto.OrderRequestDto;
import org.artmotika.tradingengineservice.exception.PriceVolatilityException;
import org.artmotika.tradingengineservice.service.TradingEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest @Testcontainers
public class TradingEngineIntegrationTest {
	@Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
	@Container static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired private TradingEngineService service;

	@Test
	void testCircuitBreakerThrowsExceptionOnVolatility() {
		OrderRequestDto req = new OrderRequestDto();
		req.setUserId("user-1"); req.setAssetId("asset-1"); req.setPrice(new BigDecimal("9999")); // Spike
		assertThrows(PriceVolatilityException.class, () -> service.consumeOrder(req));
	}
}
