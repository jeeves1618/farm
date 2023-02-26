package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Domain.PackPrice;
import org.farmfresh.RESTEndPoints.Entity.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PricingRepo extends JpaRepository<Pricing, Integer> {
    List<Pricing> findByMenuItemId(int menuItemId);

    @Query("select new org.farmfresh.RESTEndPoints.Domain.PackPrice(menuItemPackPrice) from Pricing p where p.menuItemId = ?1 and p.packSize =?2")
    PackPrice findPriceForPack(int menuItemId, String packSize);
}
