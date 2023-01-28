package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Entity.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingRepo extends JpaRepository<Pricing, Integer> {
    List<Pricing> findByMenuItemId(int menuItemId);
}
