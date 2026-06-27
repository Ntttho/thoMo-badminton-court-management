package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.BadmintonCluster;
import com.reptithcm.edu.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByClusterId(Long clusterId);

    List<Court> findByCluster(BadmintonCluster cluster);

    List<Court> findByClusterManagerId(Long managerId);

    long countByClusterId(Long clusterId);

    long countByClusterIdAndIsAvailableTrue(Long clusterId);
}
