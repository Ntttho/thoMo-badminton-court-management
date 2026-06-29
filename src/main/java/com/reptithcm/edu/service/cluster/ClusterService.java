package com.reptithcm.edu.service.cluster;

import com.reptithcm.edu.dto.request.cluster.ClusterRequest;
import com.reptithcm.edu.dto.response.cluster.ClusterResponse;
import com.reptithcm.edu.entity.BadmintonCluster;
import com.reptithcm.edu.entity.Booking;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.exception.handler.AppException;
import com.reptithcm.edu.exception.handler.ErrorCode;
import com.reptithcm.edu.repository.BadmintonClusterRepository;
import com.reptithcm.edu.repository.BookingRepository;
import com.reptithcm.edu.repository.CourtRepository;
import com.reptithcm.edu.service.common.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClusterService {
    private final BadmintonClusterRepository clusterRepository;
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<ClusterResponse> getAllClusters(int page, int size) {
        return clusterRepository.findAll(PageRequest.of(page, size)).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ClusterResponse getClusterById(Long id) {
        return mapToResponse(getCluster(id));
    }

    @Transactional(readOnly = true)
    public Page<ClusterResponse> getMyClusters(int page, int size) {
        User manager = currentUserService.getCurrentEnabledUser();
        return clusterRepository.findByManagerId(manager.getId(), PageRequest.of(page, size)).map(this::mapToResponse);
    }

    @Transactional
    public ClusterResponse createCluster(ClusterRequest request) {
        User manager = currentUserService.getCurrentEnabledUser();
        BadmintonCluster cluster = BadmintonCluster.builder()
                .name(clean(request.getName()))
                .address(clean(request.getAddress()))
                .hotLine(clean(request.getHotLine()))
                .manager(manager)
                .build();
        return mapToResponse(clusterRepository.save(cluster));
    }

    @Transactional
    public ClusterResponse updateCluster(Long id, ClusterRequest request) {
        User manager = currentUserService.getCurrentEnabledUser();
        BadmintonCluster cluster = getManagedCluster(id, manager.getId());
        cluster.setName(clean(request.getName()));
        cluster.setAddress(clean(request.getAddress()));
        cluster.setHotLine(clean(request.getHotLine()));
        return mapToResponse(clusterRepository.save(cluster));
    }

    @Transactional
    public void deleteCluster(Long id) {
        User manager = currentUserService.getCurrentEnabledUser();
        BadmintonCluster cluster = getManagedCluster(id, manager.getId());
        List<Booking> bookings = bookingRepository.findByCourtClusterId(id).stream().filter(
                book -> book.getStatus().equals("SUCCESS") && book.getBookingDate().isAfter(LocalDate.now())
        ).toList();

        // kiem tra xem có còn booking hay không trước khi xóa
        if (!bookings.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST.getCode(), "Cannot delete a cluster that already has bookings");
        }
        clusterRepository.delete(cluster);
    }

    private BadmintonCluster getCluster(Long id) {
        return clusterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND.getCode(), "Cluster not found"));
    }

    private BadmintonCluster getManagedCluster(Long id, Long managerId) {
        BadmintonCluster cluster = getCluster(id);
        if (cluster.getManager() == null || !managerId.equals(cluster.getManager().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return cluster;
    }

    private ClusterResponse mapToResponse(BadmintonCluster cluster) {
        Long clusterId = cluster.getId();
        User manager = cluster.getManager();
        return ClusterResponse.builder()
                .id(clusterId)
                .name(cluster.getName())
                .address(cluster.getAddress())
                .hotLine(cluster.getHotLine())
                .managerId(manager == null ? null : manager.getId())
                .managerUsername(manager == null ? null : manager.getUsername())
                .managerName(manager == null ? "N/A" : manager.getFullName())
                .totalCourts(courtRepository.countByClusterId(clusterId))
                .availableCourts(courtRepository.countByClusterIdAndIsAvailableTrue(clusterId))
                .build();
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}
