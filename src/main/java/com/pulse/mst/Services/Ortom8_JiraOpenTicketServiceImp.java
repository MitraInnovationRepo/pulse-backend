package com.pulse.mst.Services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pulse.mst.Entity.ApiResponseForList;
import com.pulse.mst.Entity.Audit;
import com.pulse.mst.Entity.IT_JiraOpenTicket;
import com.pulse.mst.Entity.Ortom8_JiraOpenTicket;
import com.pulse.mst.Repostory.AuditRepo;
import com.pulse.mst.Repostory.IT_JiraOpenTicketServiceRepo;
import com.pulse.mst.Repostory.OrtomOpenTicketServiceRepo;
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


@Service
public class Ortom8_JiraOpenTicketServiceImp implements   Ortom8OpenTicketService{
    static Logger logger= LoggerFactory.getLogger(Ortom8_JiraOpenTicketServiceImp.class);


    @Autowired
    OrtomOpenTicketServiceRepo ortomOpenTicketServiceRepo;


    @Autowired
    AuditRepo auditRepo;

    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.NONE)

    public ResponseEntity<?> UpdateOpenTable_Ortom8(String jiraOpenURL, String jiraUserName, String jiraUserToken) {

        Audit audit_it =new Audit();
        try {
            int startAt = 0;
            int maxResults = 50;
            int queryOutputItemsCount = 0;
            logger.info("Ortom8_tickets table is Deleting....");
            ortomOpenTicketServiceRepo.deleteAll();
            logger.info("Ortom8_tickets table  deleted.");
            audit_it.setActivityName("Ortom8_GetOpenTickets");
            audit_it.setJobStart(LocalDateTime.now());
            logger.info("Job starting to update the Ortom8_tickets table ...");

            do{
                // request url
                String url = jiraOpenURL+"&startAt="+startAt;
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

                    Ortom8_JiraOpenTicket ortom8_jiraOpenTicket =new Ortom8_JiraOpenTicket();
                    ortom8_jiraOpenTicket.setProjectName((item.getAsJsonObject().get("fields").getAsJsonObject().get("project").getAsJsonObject().get("name")).getAsString());
                    ortom8_jiraOpenTicket.setKey(item.getAsJsonObject().get("key").getAsString());
                    ortom8_jiraOpenTicket.setPriority(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString());

                    if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Highest")){
                        ortom8_jiraOpenTicket.setPriority2("P1");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("High")){
                        ortom8_jiraOpenTicket.setPriority2("P2");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Medium")){
                        ortom8_jiraOpenTicket.setPriority2("P3");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Low")){
                        ortom8_jiraOpenTicket.setPriority2("P4");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Lowest")){
                        ortom8_jiraOpenTicket.setPriority2("P5");
                    }

                    ortom8_jiraOpenTicket.setIssueType(item.getAsJsonObject().get("fields").getAsJsonObject().get("issuetype").getAsJsonObject().get("name").getAsString());
                    ortom8_jiraOpenTicket.setSummary(item.getAsJsonObject().get("fields").getAsJsonObject().get("summary").getAsString());
                    ortom8_jiraOpenTicket.setCreatorName(item.getAsJsonObject().get("fields").getAsJsonObject().get("creator").getAsJsonObject().get("displayName").getAsString());

//                    if (item.getAsJsonObject().get("fields").getAsJsonObject().get("creator").getAsJsonObject().get("displayName").getAsString() == "PagerDuty") {
//                        jiraOpenTicket.setCreatorName2("Proactive");
//                    }else {
//                        jiraOpenTicket.setCreatorName2("Reactive");
//                    }

                    String inputCreatedDate = item.getAsJsonObject().get("fields").getAsJsonObject().get("created").getAsString();
                    String outputCreatedDate = inputCreatedDate.substring(0, 19);
                    ortom8_jiraOpenTicket.setCreatedDate(LocalDateTime.parse(outputCreatedDate));

                    String CreateMonthYear =CreateMonthYear(outputCreatedDate);
                    ortom8_jiraOpenTicket.setCreatedMonthYear(CreateMonthYear);
                    long CretedAge=calculateCreateAgeDays(outputCreatedDate);
                    if(CretedAge>=0 && CretedAge<=7){
                        ortom8_jiraOpenTicket.setCreatedAge("01WK");
                    }else if(CretedAge>=8 && CretedAge<=14){
                        ortom8_jiraOpenTicket.setCreatedAge("02WK");
                    }else if(CretedAge>=15 && CretedAge<=21){
                        ortom8_jiraOpenTicket.setCreatedAge("03WK");
                    }else if(CretedAge>=21 && CretedAge<=30){
                        ortom8_jiraOpenTicket.setCreatedAge("04WK");
                    }else if(CretedAge>=31 && CretedAge<=60){
                        ortom8_jiraOpenTicket.setCreatedAge("05-08WK");
                    }else if(CretedAge>=61 && CretedAge<=90){
                        ortom8_jiraOpenTicket.setCreatedAge("09-12WK");
                    }else if(CretedAge>=91 && CretedAge<=120){
                        ortom8_jiraOpenTicket.setCreatedAge("13-16WK");
                    }else if(CretedAge>=121){
                        ortom8_jiraOpenTicket.setCreatedAge("17WK");
                    }


                    ortom8_jiraOpenTicket.setCurrentStatus(item.getAsJsonObject().get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString());
                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("assignee").isJsonNull()){
                        ortom8_jiraOpenTicket.setCurrentAssigneeName(item.getAsJsonObject().get("fields").getAsJsonObject().get("assignee").getAsJsonObject().get("displayName").getAsString());
                    }else {
                        ortom8_jiraOpenTicket.setCurrentAssigneeName("Not assignee");
                    }

                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("timespent").isJsonNull()){
                        ortom8_jiraOpenTicket.setTimeSpent(item.getAsJsonObject().get("fields").getAsJsonObject().get("timespent").getAsString());

                    }else {
                        ortom8_jiraOpenTicket.setTimeSpent("");
                    }

                    String inputUpdatedDate = item.getAsJsonObject().get("fields").getAsJsonObject().get("updated").getAsString();
                    String outputUpdatedDate = inputUpdatedDate.substring(0, 19);
                    ortom8_jiraOpenTicket.setUpdated(LocalDateTime.parse(outputUpdatedDate));

                    long UpdateAge=calculateUpdatedAgeDays(outputUpdatedDate);
                    if(UpdateAge>=0 && UpdateAge<=7){
                        ortom8_jiraOpenTicket.setUpdatedAge("01WK");
                    }else if(UpdateAge>=8 && UpdateAge<=14){
                        ortom8_jiraOpenTicket.setUpdatedAge("02WK");
                    }else if(UpdateAge>=15 && UpdateAge<=21){
                        ortom8_jiraOpenTicket.setUpdatedAge("03WK");
                    }else if(UpdateAge>=21 && UpdateAge<=30){
                        ortom8_jiraOpenTicket.setUpdatedAge("04WK");
                    }else if(UpdateAge>=31 && UpdateAge<=60){
                        ortom8_jiraOpenTicket.setUpdatedAge("05-08WK");
                    }else if(UpdateAge>=61 && UpdateAge<=90){
                        ortom8_jiraOpenTicket.setUpdatedAge("09-12WK");
                    }else if(UpdateAge>=91 && UpdateAge<=120){
                        ortom8_jiraOpenTicket.setUpdatedAge("13-16WK");
                    }else if(UpdateAge>=121){
                        ortom8_jiraOpenTicket.setUpdatedAge("17WK");
                    }

//                    jiraOpenTicket.setRootCause("");
//                    jiraOpenTicket.setClosureNote("");
                    ortomOpenTicketServiceRepo.save(ortom8_jiraOpenTicket);

                }
                startAt=startAt+maxResults;
            }while(queryOutputItemsCount>=maxResults);

        } catch (Exception ex) {
            ex.printStackTrace();
            audit_it.setJobEnd(LocalDateTime.now());
            audit_it.setStatus("Failed ");
            audit_it.setMessage(ex.getMessage());
            logger.error("Ortom8_tickets table updated failed "+ex.getMessage());
            auditRepo.save(audit_it);
            logger.info("Ortom8 audit table has not updated");
            //  return "table updated failed.....";
            return  ResponseEntity.ok(new ApiResponseForList(false, "Ortom8 Table updated failed ",Collections.emptyList()));

        }
        logger.info("Job end with succeed");

        audit_it.setJobEnd(LocalDateTime.now());
        audit_it.setStatus("Completed ");
        audit_it.setMessage("Ortom8_tickets table has been updated successfully");
        auditRepo.save(audit_it);
        logger.info("Ortom8_tickets table has been updated successfully");
        logger.info("Ortom8 audit table has been updated successfully");

        return  ResponseEntity.ok(new ApiResponseForList(true, "Ortom8 Table updated successfully", Collections.emptyList()));
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

}
