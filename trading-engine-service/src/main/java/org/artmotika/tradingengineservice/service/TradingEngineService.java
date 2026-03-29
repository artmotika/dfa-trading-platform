package org.artmotika.tradingengineservice.service;

import lombok.RequiredArgsConstructor;
import org.artmotika.common.dto.OrderRequestDto;
import org.artmotika.tradingengineservice.dto.ValidatedOrderEventDto;
import org.artmotika.tradingengineservice.exception.PriceVolatilityException;
import org.artmotika.tradingengineservice.model.Order;
import org.artmotika.tradingengineservice.model.TradeLedger;
import org.artmotika.tradingengineservice.repo.OrderRepository;
import org.artmotika.tradingengineservice.repo.TradeLedgerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradingEngineService {
    private final OrderRepository orderRepository;
    private final TradeLedgerRepository ledgerRepository;
    private final KafkaTemplate<String, ValidatedOrderEventDto> kafkaTemplate;

    @KafkaListener(topics = "orders.created", groupId = "trading-engine-group")
    public void consumeOrder(OrderRequestDto dto) {
        List<TradeLedger> lastTrades = ledgerRepository.findTop10ByOrder_Asset_IdOrderByTimestampDesc(dto.getAssetId());

        if (!lastTrades.isEmpty()) {
            BigDecimal sum = lastTrades.stream().map(TradeLedger::getExecutionPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(lastTrades.size()), 4, RoundingMode.HALF_UP);
            BigDecimal upper = avg.multiply(new BigDecimal("1.20"));
            BigDecimal lower = avg.multiply(new BigDecimal("0.80"));

            if (dto.getPrice().compareTo(upper) > 0 || dto.getPrice().compareTo(lower) < 0) {
                throw new PriceVolatilityException("Order price outside 20% volatility threshold");
            }
        }

        Order order = new Order(); // Mocking relationships for brevity
        order.setId(UUID.randomUUID().toString());
        order.setAmount(dto.getAmount());
        order.setPrice(dto.getPrice());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        ValidatedOrderEventDto event = new ValidatedOrderEventDto();
        event.setId(order.getId()); event.setUserId(dto.getUserId());
        event.setAssetId(dto.getAssetId()); event.setAmount(dto.getAmount()); event.setPrice(dto.getPrice());
        kafkaTemplate.send("orders.validated", event);
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void generateBankReport() throws Exception {
        List<TradeLedger> dailyTrades = ledgerRepository.findByTimestampAfter(LocalDateTime.now().minusDays(1));
        StringBuilder csv = new StringBuilder("ID,TxHash,Price,Time\n");
        for (TradeLedger t : dailyTrades) {
            csv.append(t.getId()).append(",").append(t.getTransactionHash()).append(",")
                    .append(t.getExecutionPrice()).append(",").append(t.getTimestamp()).append("\n");
        }
        Path path = Paths.get("/reports/daily_ledger_" + System.currentTimeMillis() + ".csv");
        Files.createDirectories(path.getParent());
        Files.writeString(path, csv.toString());
    }
}