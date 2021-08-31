package com.pulse.mst.Services;


import com.pulse.mst.Controller.ActivityAuditController;
import com.pulse.mst.Entity.Audit;
import com.pulse.mst.Repostory.AuditRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditActivityServiceServiceImp implements AuditActivityService {
    static Logger logger= LoggerFactory.getLogger(AuditActivityServiceServiceImp.class);
    @Autowired
    AuditRepo auditRepo;
    //Audit
    @Override
    public ResponseEntity<?> getLastDetails() {
        List<Audit> GET_OPEN_INCIDENT_LAST_DETAILS = auditRepo.top1TicketDetails("GetOpenTickets");
        List<Audit> GET_CLOSED_INCIDENT_LAST_DETAILS = auditRepo.top1TicketDetails("GetClosedTickets");
        List<Audit> GET_IT_INCIDENT_LAST_DETAILS = auditRepo.top1TicketDetails("IT_GetOpenTickets");
        List<Audit> GET_Ortom8_INCIDENT_LAST_DETAILS = auditRepo.top1TicketDetails("Ortom8_GetOpenTickets");
        Audit auditOpen =new Audit();
        Audit auditClosed =new Audit();
        Audit it =new Audit();
        ArrayList<Audit> ArrayData=new ArrayList<>();
        if(!GET_OPEN_INCIDENT_LAST_DETAILS.isEmpty()){
            auditOpen.setMessage(GET_OPEN_INCIDENT_LAST_DETAILS.get(0).getMessage());
            auditOpen.setId(GET_OPEN_INCIDENT_LAST_DETAILS.get(0).getId());
            auditOpen.setStatus(GET_OPEN_INCIDENT_LAST_DETAILS.get(0).getStatus());
            auditOpen.setActivityName(GET_OPEN_INCIDENT_LAST_DETAILS.get(0).getActivityName());
            auditOpen.setJobStart(GET_OPEN_INCIDENT_LAST_DETAILS.get(0).getJobStart());
            auditOpen.setJobEnd(GET_OPEN_INCIDENT_LAST_DETAILS.get(0).getJobEnd());
            ArrayData.add(auditOpen);
        }
        if(!GET_CLOSED_INCIDENT_LAST_DETAILS.isEmpty()){
            auditClosed.setMessage(GET_CLOSED_INCIDENT_LAST_DETAILS.get(0).getMessage());
            auditClosed.setId(GET_CLOSED_INCIDENT_LAST_DETAILS.get(0).getId());
            auditClosed.setStatus(GET_CLOSED_INCIDENT_LAST_DETAILS.get(0).getStatus());
            auditClosed.setActivityName(GET_CLOSED_INCIDENT_LAST_DETAILS.get(0).getActivityName());
            auditClosed.setJobStart(GET_CLOSED_INCIDENT_LAST_DETAILS.get(0).getJobStart());
            auditClosed.setJobEnd(GET_CLOSED_INCIDENT_LAST_DETAILS.get(0).getJobEnd());
            ArrayData.add(auditClosed);
        }
        if(!GET_IT_INCIDENT_LAST_DETAILS.isEmpty()){
            it.setMessage(GET_IT_INCIDENT_LAST_DETAILS.get(0).getMessage());
            it.setId(GET_IT_INCIDENT_LAST_DETAILS.get(0).getId());
            it.setStatus(GET_IT_INCIDENT_LAST_DETAILS.get(0).getStatus());
            it.setActivityName(GET_IT_INCIDENT_LAST_DETAILS.get(0).getActivityName());
            it.setJobStart(GET_IT_INCIDENT_LAST_DETAILS.get(0).getJobStart());
            it.setJobEnd(GET_IT_INCIDENT_LAST_DETAILS.get(0).getJobEnd());
            ArrayData.add(it);
        }
        if(!GET_Ortom8_INCIDENT_LAST_DETAILS.isEmpty()){
            it.setMessage(GET_Ortom8_INCIDENT_LAST_DETAILS.get(0).getMessage());
            it.setId(GET_Ortom8_INCIDENT_LAST_DETAILS.get(0).getId());
            it.setStatus(GET_Ortom8_INCIDENT_LAST_DETAILS.get(0).getStatus());
            it.setActivityName(GET_Ortom8_INCIDENT_LAST_DETAILS.get(0).getActivityName());
            it.setJobStart(GET_Ortom8_INCIDENT_LAST_DETAILS.get(0).getJobStart());
            it.setJobEnd(GET_Ortom8_INCIDENT_LAST_DETAILS.get(0).getJobEnd());
            ArrayData.add(it);
        }
        logger.info("ActivityAuditController- getLastDetails - success");
        return ResponseEntity.ok(ArrayData);

//        return audit;
    }
}
