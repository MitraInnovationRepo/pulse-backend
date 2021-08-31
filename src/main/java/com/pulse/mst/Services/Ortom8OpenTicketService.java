package com.pulse.mst.Services;

import org.springframework.http.ResponseEntity;

public interface Ortom8OpenTicketService {
    ResponseEntity<?> UpdateOpenTable_Ortom8(String jiraOpenURL, String jiraUserName, String jiraUserToken);

}
