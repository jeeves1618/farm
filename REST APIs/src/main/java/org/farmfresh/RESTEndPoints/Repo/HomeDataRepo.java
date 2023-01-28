package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Entity.HomeData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeDataRepo extends JpaRepository<HomeData, Integer> {

}
