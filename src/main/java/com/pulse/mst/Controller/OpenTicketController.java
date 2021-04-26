package com.pulse.mst.Controller;


import com.pulse.mst.Model.SearchDataRequest;
import com.pulse.mst.Services.JiraOpenTicketService;
import com.pulse.mst.Services.UserServiceImp;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

//@PropertySource(value = {"file:/home/ec2-user/DeploymentFile/ProjectDeployment"})
//@PropertySource(value = {"file:/home/App_Server/Project/MST24X7/application.properties_bk"})

@RestController
@RequestMapping("/api/ticket")
public class OpenTicketController {


    static Logger logger= LoggerFactory.getLogger(OpenTicketController.class);
    @Value("${jira.rest.api.url.getAllOpen}")
    private String jiraOpenURL;
    @Value("${jira.rest.api.userName}")
    private String jiraUserName;
    @Value("${jira.rest.api.access.token}")
    private String jiraUserToken;



    @Autowired
    JiraOpenTicketService jiraOpenTicketService;

    @RequestMapping(value = "/open",method = RequestMethod.GET)
    public ResponseEntity<?> UpdateOpenTicketTable(){
        return jiraOpenTicketService.UpdateOpenTable(jiraOpenURL,jiraUserName,jiraUserToken);
    }

    @Scheduled(cron="0 0 1 * * *", zone="Asia/Colombo")
    public void ScheduledGetAllOpenTicket() {
        logger.info("Cron job starting  automatically for UpdateOpenTicketTable at ="+LocalDateTime.now());
        UpdateOpenTicketTable();
        logger.info("Cron job end automatically for UpdateOpenTicketTable at ="+LocalDateTime.now());
    }

    @RequestMapping(value = "/get-all",method = RequestMethod.POST)
    public ResponseEntity<?> GetOpenTicketTable(@RequestBody SearchDataRequest searchDataRequest){
        logger.info("GetOpenTicketTable - start");
        return jiraOpenTicketService.GetOpenTicketTable(searchDataRequest.getSelectedProject(),searchDataRequest.getSelectedDateRange(),searchDataRequest.getSelectedStatus());
    }

    @RequestMapping(value = "/get-project",method = RequestMethod.GET)
    public ResponseEntity<?> GetProject(){
        logger.info("GetProject - start");
        return jiraOpenTicketService.GetProject();
    }

    @RequestMapping(value = "/get-ticket-status",method = RequestMethod.GET)
    public ResponseEntity<?> GetTicketStatus(){
        logger.info("GetTicketStatus - start");
        return jiraOpenTicketService.GetTicketStatus();
    }

    @RequestMapping(value = "/get-default",method = RequestMethod.POST)
    public ResponseEntity<?> GetDefaultDashboardData(@RequestBody SearchDataRequest searchDataRequest){
        logger.info("GetDefaultDashboardData - start");
        return jiraOpenTicketService.GetDefaultDashboardData(searchDataRequest.getSelectedProject(),searchDataRequest.getSelectedDateRange(),searchDataRequest.getSelectedStatus());
    }
}
