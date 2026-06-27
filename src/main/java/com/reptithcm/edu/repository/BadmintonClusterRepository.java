package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.BadmintonCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadmintonClusterRepository extends JpaRepository<BadmintonCluster, Long> {
}
