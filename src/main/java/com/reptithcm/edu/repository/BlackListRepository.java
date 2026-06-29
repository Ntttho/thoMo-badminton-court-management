package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    boolean existsByToken(String token);
}
