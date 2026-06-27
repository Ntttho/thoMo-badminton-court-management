package com.reptithcm.edu.service.court;

import com.reptithcm.edu.dto.request.court.CourtRequest;
import com.reptithcm.edu.dto.response.court.CourtResponse;
import com.reptithcm.edu.entity.BadmintonCluster;
import com.reptithcm.edu.entity.Court;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.exception.handler.AppException;
import com.reptithcm.edu.exception.handler.ErrorCode;
import com.reptithcm.edu.repository.BadmintonClusterRepository;
import com.reptithcm.edu.repository.CourtRepository;
import com.reptithcm.edu.service.common.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;
    private final BadmintonClusterRepository clusterRepository;
    private final CurrentUserService currentUserService;

    public List<CourtResponse> getAllCourts() {
        return courtRepository.findAll().stream()
                .map(this::mapToCourtResponse)
                .toList();
    }

    public List<CourtResponse> getCourtsByCluster(Long clusterId) {
        return courtRepository.findByClusterId(clusterId).stream()
                .map(this::mapToCourtResponse)
                .toList();
    }

    public CourtResponse getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return mapToCourtResponse(court);
    }

    @Transactional
    public CourtResponse createCourt(CourtRequest request) {
        User manager = currentUserService.getCurrentEnabledUser();
        BadmintonCluster cluster = clusterRepository.findById(request.getClusterId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (cluster.getManager() == null || !cluster.getManager().getId().equals(manager.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Court court = Court.builder()
                .courtName(request.getCourtName())
                .type(request.getType())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .cluster(cluster)
                .build();

        return mapToCourtResponse(courtRepository.save(court));
    }

    @Transactional
    public CourtResponse updateCourt(Long id, CourtRequest request) {
        User manager = currentUserService.getCurrentEnabledUser();
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (court.getCluster() == null || court.getCluster().getManager() == null || !court.getCluster().getManager().getId().equals(manager.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        court.setCourtName(request.getCourtName());
        court.setType(request.getType());
        court.setImageUrl(request.getImageUrl());
        if (request.getIsAvailable() != null) {
            court.setIsAvailable(request.getIsAvailable());
        }

        return mapToCourtResponse(courtRepository.save(court));
    }

    @Transactional
    public void deleteCourt(Long id) {
        User manager = currentUserService.getCurrentEnabledUser();
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (court.getCluster() == null || court.getCluster().getManager() == null || !court.getCluster().getManager().getId().equals(manager.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        courtRepository.delete(court);
    }

    private CourtResponse mapToCourtResponse(Court court) {
        BadmintonCluster cluster = court.getCluster();
        User manager = cluster != null ? cluster.getManager() : null;
        return CourtResponse.builder()
                .id(court.getId())
                .courtName(court.getCourtName())
                .type(court.getType())
                .imageUrl(court.getImageUrl())
                .isAvailable(court.getIsAvailable())
                .clusterId(cluster != null ? cluster.getId() : null)
                .clusterName(cluster != null ? cluster.getName() : "N/A")
                .managerId(manager != null ? manager.getId() : null)
                .build();
    }
}
