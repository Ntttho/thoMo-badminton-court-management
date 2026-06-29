package com.reptithcm.edu.controller.court;

import com.reptithcm.edu.dto.request.court.CourtRequest;
import com.reptithcm.edu.dto.response.ApiResponse;
import com.reptithcm.edu.dto.response.court.CourtResponse;
import com.reptithcm.edu.service.court.CourtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    @GetMapping
    public ApiResponse<List<CourtResponse>> getAllCourts() {
        return ApiResponse.success(courtService.getAllCourts());
    }

    @GetMapping("/{id}")
    public ApiResponse<CourtResponse> getCourtById(@PathVariable Long id) {
        return ApiResponse.success(courtService.getCourtById(id));
    }

    @GetMapping("/cluster/{clusterId}")
    public ApiResponse<List<CourtResponse>> getCourtsByCluster(@PathVariable Long clusterId) {
        return ApiResponse.success(courtService.getCourtsByCluster(clusterId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<CourtResponse> createCourt(
            @RequestPart("data") @Valid CourtRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(courtService.createCourt(request, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<CourtResponse> updateCourt(
            @PathVariable Long id, 
            @RequestPart("data") @Valid CourtRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(courtService.updateCourt(id, request, image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<String> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ApiResponse.success("Delete court success");
    }
}
