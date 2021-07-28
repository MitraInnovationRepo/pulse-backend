package com.pulse.mst.Services;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.*;
import com.pulse.mst.Controller.OpenTicketController;
import com.pulse.mst.Entity.*;
import com.pulse.mst.Model.SearchDataRequest;
import com.pulse.mst.Repostory.AuditRepo;
import com.pulse.mst.Repostory.JiraOpenTicketServiceRepo;
import com.pulse.mst.Repostory.ProjectRepo;
import com.pulse.mst.Repostory.TicketStatusRepo;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JiraOpenTicketServiceImp implements JiraOpenTicketService {

    @Autowired
    JiraOpenTicketServiceRepo jiraOpenTicketServiceRepo;
    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    TicketStatusRepo ticketStatusRepo;

    @Autowired
    AuditRepo auditRepo;
    static Logger logger= LoggerFactory.getLogger(JiraOpenTicketServiceImp.class);


    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.NONE)

    public ResponseEntity<?> UpdateOpenTable(String jiraOpenURL, String jiraUserName, String jiraUserToken) {
        Audit audit =new Audit();

        try {
            int startAt = 0;
            int maxResults = 50;
            int queryOutputItemsCount = 0;
            logger.info("all_open_tickets table is Deleting....");
            jiraOpenTicketServiceRepo.deleteAll();
            logger.info("all_open_tickets table  deleted.");
            audit.setActivityName("GetOpenTickets");
            audit.setJobStart(LocalDateTime.now());
            logger.info("Job starting to update the all_open_tickets table ...");

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
//                System.out.println("json data"+jsonObject);

                int n=startAt+0;
                for(JsonElement item:jsonObject.get("issues").getAsJsonArray()){

                    JiraOpenTicket jiraOpenTicket =new JiraOpenTicket();

                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("customfield_10082").isJsonNull()){
                        jiraOpenTicket.setPlaningDate(item.getAsJsonObject().get("fields").getAsJsonObject().get("customfield_10082").getAsString());
                    }

                    jiraOpenTicket.setProjectName((item.getAsJsonObject().get("fields").getAsJsonObject().get("project").getAsJsonObject().get("name")).getAsString());
                    jiraOpenTicket.setKey(item.getAsJsonObject().get("key").getAsString());
                    jiraOpenTicket.setPriority(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString());

                    if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Critical")){
                        jiraOpenTicket.setPriority2("P1");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("High")){
                        jiraOpenTicket.setPriority2("P2");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Medium")){
                        jiraOpenTicket.setPriority2("P3");
                    }else if(item.getAsJsonObject().get("fields").getAsJsonObject().get("priority").getAsJsonObject().get("name").getAsString().equals("Low")){
                        jiraOpenTicket.setPriority2("P4");
                    }

                    jiraOpenTicket.setIssueType(item.getAsJsonObject().get("fields").getAsJsonObject().get("issuetype").getAsJsonObject().get("name").getAsString());
                    jiraOpenTicket.setSummary(item.getAsJsonObject().get("fields").getAsJsonObject().get("summary").getAsString());
                    jiraOpenTicket.setCreatorName(item.getAsJsonObject().get("fields").getAsJsonObject().get("creator").getAsJsonObject().get("displayName").getAsString());

                    if (item.getAsJsonObject().get("fields").getAsJsonObject().get("creator").getAsJsonObject().get("displayName").getAsString().equals("PagerDuty")) {
                        jiraOpenTicket.setCreatorName2("Proactive");
                    }else {
                        jiraOpenTicket.setCreatorName2("Reactive");
                    }


                    String inputCreatedDate = item.getAsJsonObject().get("fields").getAsJsonObject().get("created").getAsString();
                    String outputCreatedDate = inputCreatedDate.substring(0, 19);
                    jiraOpenTicket.setCreatedDate(LocalDateTime.parse(outputCreatedDate));

                    String CreateMonthYear =CreateMonthYear(outputCreatedDate);
                    jiraOpenTicket.setCreatedMonthYear(CreateMonthYear);
                    long CretedAge=calculateCreateAgeDays(outputCreatedDate);
                    if(CretedAge>=0 && CretedAge<=7){
                        jiraOpenTicket.setCreatedAge("01WK");
                    }else if(CretedAge>=8 && CretedAge<=14){
                        jiraOpenTicket.setCreatedAge("02WK");
                    }else if(CretedAge>=15 && CretedAge<=21){
                        jiraOpenTicket.setCreatedAge("03WK");
                    }else if(CretedAge>=21 && CretedAge<=30){
                        jiraOpenTicket.setCreatedAge("04WK");
                    }else if(CretedAge>=31 && CretedAge<=60){
                        jiraOpenTicket.setCreatedAge("05-08WK");
                    }else if(CretedAge>=61 && CretedAge<=90){
                        jiraOpenTicket.setCreatedAge("09-12WK");
                    }else if(CretedAge>=91 && CretedAge<=120){
                        jiraOpenTicket.setCreatedAge("13-16WK");
                    }else if(CretedAge>=121){
                        jiraOpenTicket.setCreatedAge("17WK");
                    }


                    jiraOpenTicket.setCurrentStatus(item.getAsJsonObject().get("fields").getAsJsonObject().get("status").getAsJsonObject().get("name").getAsString());
                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("assignee").isJsonNull()){
                        jiraOpenTicket.setCurrentAssigneeName(item.getAsJsonObject().get("fields").getAsJsonObject().get("assignee").getAsJsonObject().get("displayName").getAsString());
                    }else {
                        jiraOpenTicket.setCurrentAssigneeName("Not assignee");
                    }

                    if(!item.getAsJsonObject().get("fields").getAsJsonObject().get("timespent").isJsonNull()){
                        jiraOpenTicket.setTimeSpent(item.getAsJsonObject().get("fields").getAsJsonObject().get("timespent").getAsString());

                    }else {
                        jiraOpenTicket.setTimeSpent("");
                    }

                    String inputUpdatedDate = item.getAsJsonObject().get("fields").getAsJsonObject().get("updated").getAsString();
                    String outputUpdatedDate = inputUpdatedDate.substring(0, 19);
                    jiraOpenTicket.setUpdated(LocalDateTime.parse(outputUpdatedDate));

                    long UpdateAge=calculateUpdatedAgeDays(outputUpdatedDate);
                    if(UpdateAge>=0 && UpdateAge<=7){
                        jiraOpenTicket.setUpdatedAge("01WK");
                    }else if(UpdateAge>=8 && UpdateAge<=14){
                        jiraOpenTicket.setUpdatedAge("02WK");
                    }else if(UpdateAge>=15 && UpdateAge<=21){
                        jiraOpenTicket.setUpdatedAge("03WK");
                    }else if(UpdateAge>=21 && UpdateAge<=30){
                        jiraOpenTicket.setUpdatedAge("04WK");
                    }else if(UpdateAge>=31 && UpdateAge<=60){
                        jiraOpenTicket.setUpdatedAge("05-08WK");
                    }else if(UpdateAge>=61 && UpdateAge<=90){
                        jiraOpenTicket.setUpdatedAge("09-12WK");
                    }else if(UpdateAge>=91 && UpdateAge<=120){
                        jiraOpenTicket.setUpdatedAge("13-16WK");
                    }else if(UpdateAge>=121){
                        jiraOpenTicket.setUpdatedAge("17WK");
                    }

                    jiraOpenTicket.setRootCause("");
                    jiraOpenTicket.setClosureNote("");
                    jiraOpenTicketServiceRepo.save(jiraOpenTicket);

                }
                startAt=startAt+maxResults;
            }while(queryOutputItemsCount>=maxResults);

        } catch (Exception ex) {
            ex.printStackTrace();
            audit.setJobEnd(LocalDateTime.now());
            audit.setStatus("Failed ");
            audit.setMessage(ex.getMessage());
            logger.error("all_open_tickets table updated failed "+ex.getMessage());
            auditRepo.save(audit);
            logger.info("audit table has not updated");
          //  return "table updated failed.....";
            return  ResponseEntity.ok(new ApiResponseForList(false, "Table updated failed ", Collections.emptyList()));

        }
        logger.info("Job end with succeed");

        audit.setJobEnd(LocalDateTime.now());
        audit.setStatus("Completed ");
        audit.setMessage("all_open_tickets table has been updated successfully");
        auditRepo.save(audit);
        logger.info("all_open_tickets table has been updated successfully");
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

    public ResponseEntity<?> GetProject(){

        List<Project> getAll=projectRepo.findAll();
                if(!getAll.isEmpty()){
                    logger.info("project table data fetch successfully");
                    return  ResponseEntity.ok(new ApiResponseForList(false, " project table data fetch successfully",getAll));
                }else {
                    logger.error("table empty or something went wrong");
                    return  ResponseEntity.ok(new ApiResponseForList(false, " table empty or something went wrong  ",Collections.emptyList()));

                }

    }

    public ResponseEntity<?> GetTicketStatus(){
        List<TicketStatus> getAll=ticketStatusRepo.findAll();
        if(!getAll.isEmpty()){
            logger.info("ticket table data fetch successfully");
            return  ResponseEntity.ok(new ApiResponseForList(false, " ticket table data fetch successfully",getAll));
        }else {
            logger.error("table empty or something went wrong");
            return  ResponseEntity.ok(new ApiResponseForList(false, " table empty or something went wrong  ",Collections.emptyList()));

        }

    }

    public ResponseEntity<?> GetOpenTicketTable(List<String> selectedProject, List<String> selectedDateRange, List<String> selectedStatus){
        String statusList =selectedStatus.toString().replace(", ", ",");
        String projectList =selectedProject.toString().replace(", ", ",");
        if(selectedDateRange.get(0).isEmpty()){
            List<String> EmptyList = Collections.<String>emptyList();
            if(selectedDateRange.size()>0){
                //List<Object[]>  GetDefaultDashboardData=jiraOpenTicketServiceRepo.SearchData(selectedProject.toString(),startDate1,endDate1, selectedStatus.toString());
                List<Object[]>  Open_P1=jiraOpenTicketServiceRepo.Open_P1_Search(projectList,statusList);
                List<Object[]>  All_Issues=jiraOpenTicketServiceRepo.All_Issues_Search(projectList,statusList);
                List<Object[]>  All_Open=jiraOpenTicketServiceRepo.All_Open_Search(projectList,statusList);
                List<Object[]>  All_Close=jiraOpenTicketServiceRepo.All_Close_Search(projectList,statusList);
                List<Object[]>  SearchPDData=jiraOpenTicketServiceRepo.SearchPDData2(projectList,statusList);
                List<Object[]>  GetProjectWiseSearch=jiraOpenTicketServiceRepo.GetProjectWiseSearch1(projectList,statusList);
                List<Object[]>  GetPriority=jiraOpenTicketServiceRepo.GetPriority2(projectList,statusList);


                List<Object[]>  Total_All_Issues=jiraOpenTicketServiceRepo.Total_All_Issues(projectList);
                List<Object[]>  Total_Open_P1=jiraOpenTicketServiceRepo.Total_Open_P1(projectList);
                List<Object[]>  Total_All_Open=jiraOpenTicketServiceRepo.Total_All_Open(projectList);
                List<Object[]>  Total_All_Close=jiraOpenTicketServiceRepo.Total_All_Close(projectList);


                JSONObject item = new JSONObject();
                //    item.put("GetDefaultDashboardData", GetDefaultDashboardData);
                item.put("Open_P1", Open_P1);
                item.put("All_Issues", All_Issues);
                item.put("All_Open", All_Open);
                item.put("All_Close", All_Close);
                item.put("SearchPDData", SearchPDData);
                item.put("GetProjectWise", GetProjectWiseSearch);
                item.put("GetPriority", GetPriority);

                item.put("Total_All_Issues", Total_All_Issues);
                item.put("Total_Open_P1", Total_Open_P1);
                item.put("Total_All_Open", Total_All_Open);
                item.put("Total_All_Close", Total_All_Close);
                logger.info("GetOpenTicketTable get data succeeded");
                return  ResponseEntity.ok(new ApiResponseForList(true, " get data succeeded  ",item));

            }else {
                logger.error("GetOpenTicketTable table empty or something went wrong");
                return  ResponseEntity.ok(new ApiResponseForList(false, " table empty or something went wrong  ",null));
            }
        }else {
            if(selectedDateRange.size()>0){
                LocalDate startDate1 = LocalDate.parse(selectedDateRange.get(0));
                LocalDate endDate1 = LocalDate.parse(selectedDateRange.get(1));
                //List<Object[]>  GetDefaultDashboardData=jiraOpenTicketServiceRepo.SearchData(selectedProject.toString(),startDate1,endDate1, selectedStatus.toString());
                List<Object[]>  Open_P1=jiraOpenTicketServiceRepo.Open_P1(projectList,startDate1,endDate1,statusList);
                List<Object[]>  All_Issues=jiraOpenTicketServiceRepo.All_Issues(projectList,startDate1,endDate1,statusList);
                List<Object[]>  All_Open=jiraOpenTicketServiceRepo.All_Open(projectList,startDate1,endDate1,statusList);
                List<Object[]>  All_Close=jiraOpenTicketServiceRepo.All_Close(projectList,startDate1,endDate1,statusList);
                List<Object[]>  SearchPDData=jiraOpenTicketServiceRepo.SearchPDData(projectList,startDate1,endDate1,statusList);
                List<Object[]>  GetProjectWiseSearch=jiraOpenTicketServiceRepo.GetProjectWiseSearch2(projectList,startDate1,endDate1,statusList);
                List<Object[]>  GetPriority=jiraOpenTicketServiceRepo.GetPriority(projectList,startDate1,endDate1,statusList);

                List<Object[]>  Total_All_Issues=jiraOpenTicketServiceRepo.Total_All_Issues(projectList);
                List<Object[]>  Total_Open_P1=jiraOpenTicketServiceRepo.Total_Open_P1(projectList);
                List<Object[]>  Total_All_Open=jiraOpenTicketServiceRepo.Total_All_Open(projectList);
                List<Object[]>  Total_All_Close=jiraOpenTicketServiceRepo.Total_All_Close(projectList);


                JSONObject item = new JSONObject();
                //    item.put("GetDefaultDashboardData", GetDefaultDashboardData);
                item.put("Open_P1", Open_P1);
                item.put("All_Issues", All_Issues);
                item.put("All_Open", All_Open);
                item.put("All_Close", All_Close);
                item.put("SearchPDData", SearchPDData);
                item.put("GetProjectWise", GetProjectWiseSearch);
                item.put("GetPriority", GetPriority);

                item.put("Total_All_Issues", Total_All_Issues);
                item.put("Total_Open_P1", Total_Open_P1);
                item.put("Total_All_Open", Total_All_Open);
                item.put("Total_All_Close", Total_All_Close);
                logger.info("GetOpenTicketTable get data succeeded");

                return  ResponseEntity.ok(new ApiResponseForList(true, " get data succeeded  ",item));

            }else {
                logger.error("GetOpenTicketTable table empty or something went wrong");
                return  ResponseEntity.ok(new ApiResponseForList(false, " table empty or something went wrong  ",null));
            }
        }
    }

    public ResponseEntity<?> GetDefaultDashboardData(List<String> selectedProject, List<String> selectedDateRange, List<String> selectedStatus){
        String statusList =selectedStatus.toString().replace(", ", ",");
        String projectList =selectedProject.toString().replace(", ", ",");

        if(selectedDateRange.size()>0){
            LocalDate startDate1 = LocalDate.parse(selectedDateRange.get(0));
            LocalDate endDate1 = LocalDate.parse(selectedDateRange.get(1));
           // List<Object[]>  GetDefaultDashboardData=jiraOpenTicketServiceRepo.SearchData(projectList,startDate1,endDate1, statusList);
            List<Object[]>  Open_P1=jiraOpenTicketServiceRepo.Open_P1(projectList,startDate1,endDate1,statusList);
            List<Object[]>  All_Issues=jiraOpenTicketServiceRepo.All_Issues(projectList,startDate1,endDate1,statusList);
            List<Object[]>  All_Open=jiraOpenTicketServiceRepo.All_Open(projectList,startDate1,endDate1,statusList);
            List<Object[]>  All_Close=jiraOpenTicketServiceRepo.All_Close(projectList,startDate1,endDate1,statusList);
            List<Object[]>  GetPriority=jiraOpenTicketServiceRepo.GetPriority(projectList,startDate1,endDate1,statusList);

            List<Object[]>  SearchPDData=jiraOpenTicketServiceRepo.SearchPDData(projectList,startDate1,endDate1,statusList);

            List<Object[]>  Total_All_Issues=jiraOpenTicketServiceRepo.Total_All_Issues(projectList);
            List<Object[]>  GetProjectWise=jiraOpenTicketServiceRepo.GetProjectWise(projectList);
            List<Object[]>  Total_Open_P1=jiraOpenTicketServiceRepo.Total_Open_P1(projectList);
            List<Object[]>  Total_All_Open=jiraOpenTicketServiceRepo.Total_All_Open(projectList);
            List<Object[]>  Total_All_Close=jiraOpenTicketServiceRepo.Total_All_Close(projectList);

            JSONObject item = new JSONObject();
           // item.put("GetDefaultDashboardData", GetDefaultDashboardData);
            item.put("Open_P1", Open_P1);
            item.put("All_Issues", All_Issues);
            item.put("All_Open", All_Open);
            item.put("All_Close", All_Close);
            item.put("SearchPDData", SearchPDData);
            item.put("Total_All_Issues", Total_All_Issues);
            item.put("Total_Open_P1", Total_Open_P1);
            item.put("Total_All_Open", Total_All_Open);
            item.put("Total_All_Close", Total_All_Close);
            item.put("GetProjectWise", GetProjectWise);
            item.put("GetPriority", GetPriority);
            logger.info("GetOpenTicketTable get data succeeded");
            return  ResponseEntity.ok(new ApiResponseForList(true, " get data succeeded  ",item));

        }else {
            logger.error("GetOpenTicketTable table empty or something went wrong");
            return  ResponseEntity.ok(new ApiResponseForList(false, " table empty or something went wrong  ",null));
        }




    }

}
