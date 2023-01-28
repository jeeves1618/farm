package org.farmfresh.RESTEndPoints.Repo;

import org.farmfresh.RESTEndPoints.Entity.UploadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadServiceRepo extends JpaRepository<UploadInfo, Integer> {

}
