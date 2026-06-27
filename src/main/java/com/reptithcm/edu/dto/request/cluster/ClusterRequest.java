package com.reptithcm.edu.dto.request.cluster;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClusterRequest {
    @NotBlank(message = "Cluster name is required")
    @Size(max = 100, message = "Cluster name must be at most 100 characters")
    private String name;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;

    @Size(max = 20, message = "Hot line must be at most 20 characters")
    private String hotLine;
}
