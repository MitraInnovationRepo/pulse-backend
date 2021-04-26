package com.pulse.mst.Services;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.pulse.mst.Controller.ClosedTicketsController;
import com.pulse.mst.Entity.ApiResponseForList;
import com.pulse.mst.Entity.Audit;
import com.pulse.mst.Entity.JiraClosedTicket;
import com.pulse.mst.Entity.JiraOpenTicket;
import com.pulse.mst.Repostory.AuditRepo;
import com.pulse.mst.Repostory.JiraClosedTicketServiceRepo;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JiraClosedTicketServiceImp implements JiraClosedTicketService {
    static Logger logger= LoggerFactory.getLogger(JiraClosedTicketServiceImp.class);

    @Autowired
    JiraClosedTicketServiceRepo jiraClosedTicketServiceRepo;

    @Autowired
    AuditRepo auditRepo;

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.NONE)

    public  ResponseEntity<?> UpdateClosedTable(String jiraClosedURL, String jiraUserName, String jiraUserToken) {
        Audit audit =new Audit();


        try {
            int startAt = 0;
            int maxResults = 50;
            int queryOutputItemsCount = 0;
            logger.info("all_closed_tickets table is Deleting....");
            jiraClosedTicketServiceRepo.deleteAll();
            logger.info("all_closed_tickets table  deleted.");
            audit.setActivityName("GetClosedTickets");
            audit.setJobStart(LocalDateTime.now());
            logger.info("Job starting to update the all_closed_tickets table ...");

            do{
                    // request url
                    String url = jiraClosedURL+"&startAt="+startAt;
                    // create auth credentials
                    String authStr = jiraUserName+":"+jiraUserToken;
                    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
                    // create headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Basic " + base64Creds);
                    // create request
                    HttpEntity request = new HttpEntity(headers);
                    // make a request
                    ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
                    //convert to json
                    JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
                    queryOutputItemsCount = jsonObject.get("issues").getAsJsonArray().size();

                int n=startAt+0;
                for(JsonElement item:jsonObject.get("issues").getAsJsonArray()){

                    JiraClosedTicket jiraClosedTicket =new JiraClosedTicket();
                    jiraClosedTicket.setProjectName((item.getAsJsonObject().get("fields").getAsJsonObject().get("project").getAsJsonObject().get("name")).getAsString());
                    jiraClosedTicket.setKey(item.getAsJsonObject().get("key").getAsString());
                    jiraClosedTicket.setPriority(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString());
                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("customfield_10082").isJsonNull()){
                        jiraClosedTicket.setPlaningDate(item.getAsJsonObject().get("fields").getAsJsonObject().get("customfield_10082").getAsString());
                    }
                    if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Critical")){
                        jiraClosedTicket.setPriority2("P1");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("High")){
                        jiraClosedTicket.setPriority2("P2");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Medium")){
                        jiraClosedTicket.setPriority2("P3");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Low")){
                        jiraClosedTicket.setPriority2("P4");
                    }

                    jiraClosedTicket.setIssueType(item.getAsJsonObject().get("fields").getAsJsonObject().get("issuetype").getAsJsonObject().get("name").getAsString());
                    jiraClosedTicket.setSummary(item.getAsJsonObject().get("fields").getAsJsonObject().get("summary").getAsString());
                    jiraClosedTicket.setCreatorName(item.getAsJsonObject().get("fields").getAsJsonObject().get("creator").getAsJsonObject().get("displayName").getAsString());

                    if (item.getAsJsonObject().get("fields").getAsJsonObject().get("creator").getAsJsonObject().get("displayName").getAsString() == "PagerDuty") {
                        jiraClosedTicket.setCreatorName2("Proactive");
                    }else {
                        jiraClosedTicket.setCreatorName2("Reactive");
                    }

                    String inputCreatedDate = item.getAsJsonObject().get("fields").getAsJsonObject().get("created").getAsString();
                    String outputCreatedDate = inputCreatedDate.substring(0, 19);
                    jiraClosedTicket.setCreatedDate(LocalDateTime.parse(outputCreatedDate));

                    String CreateMonthYear =CreateMonthYear(outputCreatedDate);
                    jiraClosedTicket.setCreatedMonthYear(CreateMonthYear);
                    long CretedAge=calculateCreateAgeDays(outputCreatedDate);
                    if(CretedAge>=0 && CretedAge<=7){
                        jiraClosedTicket.setCreatedAge("01WK");
                    }else if(CretedAge>=8 && CretedAge<=14){
                        jiraClosedTicket.setCreatedAge("02WK");
                    }else if(CretedAge>=15 && CretedAge<=21){
                        jiraClosedTicket.setCreatedAge("03WK");
                    }else if(CretedAge>=21 && CretedAge<=30){
                        jiraClosedTicket.setCreatedAge("04WK");
                    }else if(CretedAge>=31 && CretedAge<=60){
                        jiraClosedTicket.setCreatedAge("05-08WK");
                    }else if(CretedAge>=61 && CretedAge<=90){
                        jiraClosedTicket.setCreatedAge("09-12WK");
                    }else if(CretedAge>=91 && CretedAge<=120){
                        jiraClosedTicket.setCreatedAge("13-16WK");
                    }else if(CretedAge>=121){
                        jiraClosedTicket.setCreatedAge("17WK");
                    }


                    jiraClosedTicket.setCurrentStatus(item.getAsJsonObject().get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString());
                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("assignee").isJsonNull()){
                        jiraClosedTicket.setCurrentAssigneeName(item.getAsJsonObject().get("fields").getAsJsonObject().get("assignee").getAsJsonObject().get("displayName").getAsString());
                    }else {
                        jiraClosedTicket.setCurrentAssigneeName("Not assignee");
                    }

                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("timespent").isJsonNull()){
                        jiraClosedTicket.setTimeSpent(item.getAsJsonObject().get("fields").getAsJsonObject().get("timespent").getAsString());

                    }else {
                        jiraClosedTicket.setTimeSpent("");
                    }

                    String inputUpdatedDate = item.getAsJsonObject().get("fields").getAsJsonObject().get("created").getAsString();
                    String outputUpdatedDate = inputUpdatedDate.substring(0, 19);
                    jiraClosedTicket.setUpdated(LocalDateTime.parse(outputUpdatedDate));

                    long UpdateAge=calculateUpdatedAgeDays(outputUpdatedDate);
                    if(UpdateAge>=0 && UpdateAge<=7){
                        jiraClosedTicket.setUpdatedAge("01WK");
                    }else if(UpdateAge>=8 && UpdateAge<=14){
                        jiraClosedTicket.setUpdatedAge("02WK");
                    }else if(UpdateAge>=15 && UpdateAge<=21){
                        jiraClosedTicket.setUpdatedAge("03WK");
                    }else if(UpdateAge>=21 && UpdateAge<=30){
                        jiraClosedTicket.setUpdatedAge("04WK");
                    }else if(UpdateAge>=31 && UpdateAge<=60){
                        jiraClosedTicket.setUpdatedAge("05-08WK");
                    }else if(UpdateAge>=61 && UpdateAge<=90){
                        jiraClosedTicket.setUpdatedAge("09-12WK");
                    }else if(UpdateAge>=91 && UpdateAge<=120){
                        jiraClosedTicket.setUpdatedAge("13-16WK");
                    }else if(UpdateAge>=121){
                        jiraClosedTicket.setUpdatedAge("17WK");
                    }

                    jiraClosedTicket.setRootCause("");
                    jiraClosedTicket.setClosureNote("");
                    jiraClosedTicketServiceRepo.save(jiraClosedTicket);

                }
                startAt=startAt+maxResults;
            }while(queryOutputItemsCount>=maxResults);

        } catch (Exception ex) {
            ex.printStackTrace();
            audit.setJobEnd(LocalDateTime.now());
            audit.setStatus("Failed ");
            audit.setMessage(ex.getMessage());
            logger.error("all_closed_tickets table updated failed "+ex.getMessage());
            auditRepo.save(audit);
            logger.info("audit table has not updated");
            return  ResponseEntity.ok(new ApiResponseForList(false, "Table updated failed ", Collections.emptyList()));

        }
        logger.info("Job end with succeed");

        audit.setJobEnd(LocalDateTime.now());
        audit.setStatus("Completed ");
        audit.setMessage("all_closed_tickets table has been updated successfully");
        auditRepo.save(audit);
        logger.info("all_closed_tickets table has been updated successfully");
        logger.info("audit table has been updated successfully");

        return  ResponseEntity.ok(new ApiResponseForList(true, "Table updated successfully",Collections.emptyList()));

    }

    public long calculateCreateAgeDays(String outputCreatedDate){
        String dateStart = LocalDateTime.parse(outputCreatedDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String dateStop = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();

        long diffDAys = diff / (60 * 60 * 1000*24);
     //   System.out.println("Time in diffDAys: " + diffDAys + " diffDAys.");
        return diffDAys;
    }

    public long calculateUpdatedAgeDays(String outputUpdatedDate){
        String dateStart = LocalDateTime.parse(outputUpdatedDate).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String dateStop = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();

        long diffDAys = diff / (60 * 60 * 1000*24);
       // System.out.println("Time in diffDAys: " + diffDAys + " diffDAys.");
        return diffDAys;
    }

    public String CreateMonthYear(String outputUpdatedDate){
        String dateStart = LocalDateTime.parse(outputUpdatedDate).format(DateTimeFormatter.ofPattern("MMM-YY"));
        return dateStart;
    }

    public ResponseEntity<?> GetClosedTicketTable(){
        List<JiraClosedTicket> getAll=jiraClosedTicketServiceRepo.findByOrderByCreatedDateDesc();
        if(!getAll.isEmpty()){
            logger.info("GetClosedTicketTable Fetch closed ticket Table successfully");
            return  ResponseEntity.ok(new ApiResponseForList(true, " Fetch closed ticket Table successfully ",getAll));
        }
        else{
            logger.error("GetClosedTicketTable table empty or something went wrong");
            return  ResponseEntity.ok(new ApiResponseForList(false, " table empty or something went wrong  ",getAll));

        }

    }
}
