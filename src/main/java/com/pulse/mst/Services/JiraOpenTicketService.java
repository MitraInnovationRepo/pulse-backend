package com.pulse.mst.Services;


import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface JiraOpenTicketService {

    ResponseEntity<?> UpdateOpenTable(String jiraOpenURL, String jiraUserName, String jiraUserToken);

   // ResponseEntity<?> GetOpenTicketTable();

    ResponseEntity<?> GetProject();

    ResponseEntity<?> GetTicketStatus();

    ResponseEntity<?> GetOpenTicketTable(List<String> selectedProject, List<String> selectedDateRange, List<String> selectedStatus);

    ResponseEntity<?> GetDefaultDashboardData(List<String> selectedProject, List<String> selectedDateRange, List<String> selectedStatus);
}
