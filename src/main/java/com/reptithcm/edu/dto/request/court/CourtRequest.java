package com.reptithcm.edu.dto.request.court;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourtRequest {
    @NotBlank(message = "Court name is required")
    @Size(max = 50, message = "Court name must be at most 50 characters")
    private String courtName;

    @Size(max = 50, message = "Court type must be at most 50 characters")
    private String type;

    @Size(max = 255, message = "Image URL must be at most 255 characters")
    private String imageUrl;

    private Boolean isAvailable;

    @NotNull(message = "Cluster id is required")
    private Long clusterId;
}
