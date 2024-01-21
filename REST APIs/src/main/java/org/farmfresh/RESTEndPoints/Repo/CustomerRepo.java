package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {

    public Customer findByCustomerUserId(String customerUserId);

}
