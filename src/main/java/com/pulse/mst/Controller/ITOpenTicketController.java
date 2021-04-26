package com.pulse.mst.Controller;


import com.pulse.mst.Services.IT_JiraOpenTicketService;
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
@RequestMapping("/api/it//ticket")
public class ITOpenTicketController {
    static Logger logger= LoggerFactory.getLogger(ITOpenTicketController.class);

    @Value("${jira.rest.it.api.url.getAllOpen}")
    private String jiraOpenURL;
    @Value("${jira.rest.it.api.userName}")
    private String jiraUserName;
    @Value("${jira.rest.it.access.token}")
    private String jiraUserToken;

    @Autowired
    IT_JiraOpenTicketService it_jiraOpenTicketService;
    @RequestMapping(value = "/open",method = RequestMethod.GET)
    public ResponseEntity<?> UpdateOpenITTicketTable(){
        logger.info("UpdateOpenITTicketTable - start");
        return it_jiraOpenTicketService.UpdateOpenTable_IT(jiraOpenURL,jiraUserName,jiraUserToken);
    }

    @Scheduled(cron="0 0 3 * * *", zone="Asia/Colombo")
    public void ScheduledGetAllUpdateOpenITTicketTable() {
        logger.info("Cron job starting  automatically for UpdateOpenITTicketTable at ="+ LocalDateTime.now());
        UpdateOpenITTicketTable();
        logger.info("Cron job end automatically for UpdateOpenITTicketTable at ="+LocalDateTime.now());
    }
}
