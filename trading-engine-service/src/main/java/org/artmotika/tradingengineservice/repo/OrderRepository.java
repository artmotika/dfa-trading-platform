package org.artmotika.tradingengineservice.repo;

import org.artmotika.tradingengineservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {}
