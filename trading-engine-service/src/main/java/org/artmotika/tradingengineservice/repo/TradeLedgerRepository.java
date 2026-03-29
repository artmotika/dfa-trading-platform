package org.artmotika.tradingengineservice.repo;

import org.artmotika.tradingengineservice.model.TradeLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeLedgerRepository extends JpaRepository<TradeLedger, String> {
    List<TradeLedger> findTop10ByOrder_Asset_IdOrderByTimestampDesc(String assetId);
    List<TradeLedger> findByTimestampAfter(LocalDateTime time);
}
