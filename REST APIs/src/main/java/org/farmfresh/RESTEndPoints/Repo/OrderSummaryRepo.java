package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Entity.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderSummaryRepo extends JpaRepository<OrderSummary, Integer> {

    public List<OrderSummary> findByCustomerId(String customerId);
}
