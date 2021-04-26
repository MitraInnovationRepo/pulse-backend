package com.pulse.mst.Services;

import org.springframework.http.ResponseEntity;

public interface IT_JiraOpenTicketService {

    ResponseEntity<?> UpdateOpenTable_IT(String jiraOpenURL, String jiraUserName, String jiraUserToken);

}
