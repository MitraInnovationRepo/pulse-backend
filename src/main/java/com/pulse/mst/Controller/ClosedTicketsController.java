package com.pulse.mst.Controller;


import com.pulse.mst.Services.JiraClosedTicketService;
import org.apache.logging.log4j.LogManager;
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

//@PropertySource(value = {"file:/home/ec2-user/DeploymentFile/ProjectDeployment"})
//@PropertySource(value = {"file:/home/App_Server/Project/MST24X7/application.properties_bk"})

@RestController
@RequestMapping("/api/ticket")
public class ClosedTicketsController {

    static Logger logger= LoggerFactory.getLogger(ClosedTicketsController.class);

    @Value("${jira.rest.api.url.getAllClosed}")
    private String jiraClosedURL;
    @Value("${jira.rest.api.userName}")
    private String jiraUserName;
    @Value("${jira.rest.api.access.token}")
    private String jiraUserToken;

    @Autowired
    JiraClosedTicketService jiraClosedTicketService;

    @RequestMapping(value = "/closed",method = RequestMethod.GET)
    public ResponseEntity<?> UpdateClosedTicketTable(){
        logger.info("UpdateClosedTicketTable - start");
        return jiraClosedTicketService.UpdateClosedTable(jiraClosedURL,jiraUserName,jiraUserToken);
    }

    @Scheduled(cron="0 0 2 * * *", zone="Asia/Colombo")
    public void ScheduledGetAllClosedTicketTable() {
        logger.info("Cron job starting  automatically for UpdateClosedTicketTable at ="+ LocalDateTime.now());
        UpdateClosedTicketTable();
        logger.info("Cron job end automatically for UpdateClosedTicketTable at ="+LocalDateTime.now());
    }
    @RequestMapping(value = "/get-all-closed",method = RequestMethod.GET)
    public ResponseEntity<?> GetClosedTicketTable(){
        logger.info("GetClosedTicketTable - start");
        return jiraClosedTicketService.GetClosedTicketTable();
    }
}
