package com.reptithcm.edu.controller.cluster;

import com.reptithcm.edu.dto.request.cluster.ClusterRequest;
import com.reptithcm.edu.dto.response.ApiResponse;
import com.reptithcm.edu.dto.response.cluster.ClusterResponse;
import com.reptithcm.edu.service.cluster.ClusterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clusters")
@RequiredArgsConstructor
public class ClusterController {

    private final ClusterService clusterService;

    @GetMapping
    public ApiResponse<Page<ClusterResponse>> getAllClusters(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                             @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return ApiResponse.success(clusterService.getAllClusters(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ClusterResponse> getClusterById(@PathVariable Long id) {
        return ApiResponse.success(clusterService.getClusterById(id));
    }

    @GetMapping("/my-clusters")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<Page<ClusterResponse>> getMyClusters(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.success(clusterService.getMyClusters(page, size));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<ClusterResponse> createCluster(@RequestBody @Valid ClusterRequest request) {
        return ApiResponse.success(clusterService.createCluster(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<ClusterResponse> updateCluster(@PathVariable Long id, @RequestBody @Valid ClusterRequest request) {
        return ApiResponse.success(clusterService.updateCluster(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<String> deleteCluster(@PathVariable Long id) {
        clusterService.deleteCluster(id);
        return ApiResponse.success("Delete cluster success");
    }
}
