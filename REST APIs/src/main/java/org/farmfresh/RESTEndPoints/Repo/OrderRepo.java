package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Domain.BlockedQty;
import org.farmfresh.RESTEndPoints.Domain.CartsSummary;
import org.farmfresh.RESTEndPoints.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findByDisplayOrderId(int displayOrderId);

    @Query("select new org.farmfresh.RESTEndPoints.Domain.BlockedQty(menuItemId, sum(standardizedQuantity)) from Order O where O.menuItemId = ?1 group by menuItemId")
    public BlockedQty findSumByMenuId(int menuItemId);

    @Query("select new org.farmfresh.RESTEndPoints.Domain.CartsSummary(menuItemId, packSize, sum(menuItemCount)) from Cart C group by menuItemId, packSize")
    public List<CartsSummary> findCountByMenuIdAndPackSize();
}
