package com.pulse.mst.Services;

import com.pulse.mst.Entity.*;
import com.pulse.mst.Repostory.AgeingIssueTypeRepo;
import com.pulse.mst.Repostory.AgeingRepo;
import com.pulse.mst.Repostory.JiraOpenTicketServiceRepo;
import com.pulse.mst.Repostory.UserRepo;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AgeingServiceImp implements AgeingService{

    static Logger logger= LoggerFactory.getLogger(AgeingServiceImp.class);

    @Autowired
    AgeingRepo ageingRepo;

    @Autowired
    AgeingIssueTypeRepo ageingIssueTypeRepo;

    @Autowired
    JiraOpenTicketServiceRepo jiraOpenTicketServiceRepo;

    public ResponseEntity<?> GetAgeingName(){
        List<Ageing> getAll=ageingRepo.GetActiveAgeName(1);
        if(!getAll.isEmpty()){
            logger.info("GetAgeingName data fetch successfully");
            return  ResponseEntity.ok(new ApiResponseForList(false, " GetAgeingName table data fetch successfully",getAll));
        }else {
            logger.error("GetAgeingName table empty or something went wrong");
            return  ResponseEntity.ok(new ApiResponseForList(false, " GetAgeingName empty or something went wrong  ", Collections.emptyList()));
        }
    }

    public ResponseEntity<?> GetAgeingIssueType(){
        List<AgeingIssueType> getAll=ageingIssueTypeRepo.GetActiveAgeingIssueType(1);
        if(!getAll.isEmpty()){
            logger.info("GetAgeingIssueType data fetch successfully");
            return  ResponseEntity.ok(new ApiResponseForList(false, " GetAgeingIssueType table data fetch successfully",getAll));
        }else {
            logger.error("GetAgeingIssueType table empty or something went wrong");
            return  ResponseEntity.ok(new ApiResponseForList(false, " GetAgeingIssueType table empty or something went wrong  ", Collections.emptyList()));
        }
    }

    public ResponseEntity<?> GetAgeSearchData(List<String> selectedProject, List<String> selectedDateRange, List<String> selectedStatus,List<String> selectedIssueType, List<String> selectedAge){
        String statusList =selectedStatus.toString().replace(", ", ",");
        String projectList =selectedProject.toString().replace(", ", ",");
        String selectedIssueTypeList =selectedIssueType.toString().replace(", ", ",");
        String selectedAgeList =selectedAge.toString().replace(", ", ",");

        System.out.println("statusList="+statusList);
        System.out.println("projectList="+projectList);
        System.out.println("selectedIssueTypeList="+selectedIssueTypeList);
        System.out.println("selectedAgeList="+selectedAgeList);
        System.out.println("selectedDateRange="+selectedDateRange);


        if(!selectedDateRange.get(0).isEmpty()){
            LocalDate startDate1 = LocalDate.parse(selectedDateRange.get(0));
            LocalDate endDate1 = LocalDate.parse(selectedDateRange.get(1));

            List<Object[]>getAll=jiraOpenTicketServiceRepo.GetSearchAgeReport(projectList,startDate1,endDate1,statusList,selectedIssueTypeList,selectedAgeList);

            System.out.println("size="+getAll.size());
            JSONObject item = new JSONObject();
            item.put("AgeSearchData",getAll);
            return  ResponseEntity.ok(new ApiResponseForList(true, " GetAgeingIssueType table data fetch  successfully  ", item));

        }else {
            List<Object[]>getAll=jiraOpenTicketServiceRepo.GetSearchAgeReportBynullDate(projectList,statusList,selectedIssueTypeList,selectedAgeList);

            System.out.println("size="+getAll.size());
            JSONObject item = new JSONObject();
            item.put("AgeSearchData",getAll);
            return  ResponseEntity.ok(new ApiResponseForList(true, " GetAgeingIssueType table data fetch successfully  ", item));

        }


    }
}
