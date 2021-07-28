package com.pulse.mst.Services;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AgeingService {
    ResponseEntity<?> GetAgeingName();
    ResponseEntity<?> GetAgeingIssueType();

    ResponseEntity<?> GetAgeSearchData(List<String> selectedProject, List<String> selectedDateRange, List<String> selectedStatus,List<String> selectedIssueType, List<String> selectedAge);
}
