package org.artmotika.tradingengineservice.repo;

import org.artmotika.tradingengineservice.model.InvestorLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestorLimitRepository extends JpaRepository<InvestorLimit, String> {
}
