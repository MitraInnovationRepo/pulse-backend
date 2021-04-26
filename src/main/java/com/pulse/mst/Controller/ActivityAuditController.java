package com.pulse.mst.Controller;



import com.pulse.mst.Services.AuditActivityService;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//@PropertySource(value = {"file:/home/ec2-user/DeploymentFile/ProjectDeployment"})

@RestController
@RequestMapping("/api/audit")
public class ActivityAuditController {

    static Logger logger= LoggerFactory.getLogger(ActivityAuditController.class);

    @Autowired
    AuditActivityService auditActivityService;

    @RequestMapping(value = "/getLastDetails",method = RequestMethod.GET)
    public ResponseEntity<?> getLastDetails(){
        logger.info("ActivityAuditController- getLastDetails - start");
        return (ResponseEntity<?>) auditActivityService.getLastDetails();
    }
}
