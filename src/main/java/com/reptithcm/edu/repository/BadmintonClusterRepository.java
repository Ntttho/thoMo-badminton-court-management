package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.BadmintonCluster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadmintonClusterRepository extends JpaRepository<BadmintonCluster, Long> {
    Page<BadmintonCluster> findByManagerId(Long managerId, Pageable pageable);

    @Override
    Page<BadmintonCluster> findAll(Pageable pageable);

    boolean existsByIdAndManagerId(Long id, Long managerId);
}
