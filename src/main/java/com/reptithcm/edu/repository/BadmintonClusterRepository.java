package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.BadmintonCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadmintonClusterRepository extends JpaRepository<BadmintonCluster, Long> {
    List<BadmintonCluster> findByManagerId(Long managerId);

    boolean existsByIdAndManagerId(Long id, Long managerId);
}
