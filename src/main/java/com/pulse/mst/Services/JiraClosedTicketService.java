package com.pulse.mst.Services;


import org.springframework.http.ResponseEntity;

public interface JiraClosedTicketService {

    ResponseEntity<?> UpdateClosedTable(String jiraClosedURL, String jiraUserName, String jiraUserToken);

    ResponseEntity<?> GetClosedTicketTable();
}
