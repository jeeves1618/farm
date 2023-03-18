package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Domain.CartCount;
import org.farmfresh.RESTEndPoints.Domain.CartCustItemCount;
import org.farmfresh.RESTEndPoints.Domain.CartSummary;
import org.farmfresh.RESTEndPoints.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepo extends JpaRepository<Cart, Integer> {
    public List<Cart> findByCustomerId(String customerId);

    @Query("select new org.farmfresh.RESTEndPoints.Domain.CartSummary(menuItemId, count(*)) from Cart C where C.menuItemId = ?1 group by menuItemId")
    CartSummary findCountByMenuId(int menuItemId);

    @Query("select new org.farmfresh.RESTEndPoints.Domain.CartCount(customerId, count(*)) from Cart C where C.customerId = ?1 group by customerId")
    CartCount findCartCount(String customerId);

    @Query("select new org.farmfresh.RESTEndPoints.Domain.CartCustItemCount(customerId, menuItemId, count(*)) from Cart C where C.menuItemId = ?1 and C.customerId = ?2 group by customerId, menuItemId")
    CartCustItemCount findCartCountByItemCust(int menuItemId, String customerId);
}
