package org.artmotika.tradingengineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TradingEngineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingEngineServiceApplication.class, args);
	}

}
