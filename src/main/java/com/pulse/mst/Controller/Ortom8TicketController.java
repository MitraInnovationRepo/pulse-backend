package com.pulse.mst.Controller;


import com.pulse.mst.Services.Ortom8OpenTicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ortom8/ticket")
public class Ortom8TicketController {
    static Logger logger= LoggerFactory.getLogger(Ortom8TicketController.class);

    @Value("${jira.rest.ortom8.api.url.getAllTicketDetails}")
    private String jiraOpenURL;
    @Value("${jira.rest.ortom8.api.userName}")
    private String jiraUserName;
    @Value("${jira.rest.ortom8.access.token}")
    private String jiraUserToken;

    @Autowired
    Ortom8OpenTicketService ortom8OpenTicketService;
    @RequestMapping(value = "/open",method = RequestMethod.GET)
    public ResponseEntity<?> UpdateOpenOrtom8TicketTable(){
        logger.info("UpdateOpenOrtom8TicketTable - start");
        return ortom8OpenTicketService.UpdateOpenTable_Ortom8(jiraOpenURL,jiraUserName,jiraUserToken);
    }

    @Scheduled(cron="0 0 4 * * *", zone="Asia/Colombo")
    public void ScheduledGetAllUpdateOpenOrtom8TicketTable() {
        logger.info("Cron job starting  automatically for UpdateOpenOrtom8TicketTable at ="+ LocalDateTime.now());
        UpdateOpenOrtom8TicketTable();
        logger.info("Cron job end automatically for UpdateOpenOrtom8TicketTable at ="+LocalDateTime.now());
    }
}
