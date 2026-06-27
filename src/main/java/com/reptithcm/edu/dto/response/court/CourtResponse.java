package com.reptithcm.edu.dto.response.court;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourtResponse {
    private Long id;
    private String courtName;
    private String type;
    private String imageUrl;
    private Boolean isAvailable;
    private Long clusterId;
    private String clusterName;
    private Long managerId;
}
