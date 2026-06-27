package com.reptithcm.edu.dto.response.cluster;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClusterResponse {
    private Long id;
    private String name;
    private String address;
    private String hotLine;
    private Long managerId;
    private String managerUsername;
    private String managerName;
    private long totalCourts;
    private long availableCourts;
}
