package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, Integer> {

}
