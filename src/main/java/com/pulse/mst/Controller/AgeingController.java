package com.pulse.mst.Controller;


import com.pulse.mst.Model.SearchDataRequest;
import com.pulse.mst.Repostory.JiraOpenTicketServiceRepo;
import com.pulse.mst.Services.AgeingService;
import com.pulse.mst.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ageing")
public class AgeingController {

    static Logger logger= LoggerFactory.getLogger(AgeingController.class);

    @Autowired
    AgeingService ageingService;




    @RequestMapping(value = "/get-ageing-name",method = RequestMethod.GET)
    public ResponseEntity<?> GetAgeingName(){
        logger.info("GetAgeingName - start");
        return ageingService.GetAgeingName();
    }
    @RequestMapping(value = "/get-ageing-issue-type",method = RequestMethod.GET)
    public ResponseEntity<?> GetAgeingIssueType(){
        logger.info("GetAgeingIssueType - start");
        return ageingService.GetAgeingIssueType();
    }
    @RequestMapping(value = "/get-Search-data",method = RequestMethod.POST)
    public ResponseEntity<?> GetAgeSearchData(@RequestBody SearchDataRequest searchDataRequest){
        logger.info("GetAgeSearchData - start");
        return ageingService.GetAgeSearchData(searchDataRequest.getSelectedProject(),searchDataRequest.getSelectedDateRange(),searchDataRequest.getSelectedStatus(),searchDataRequest.getSelectedIssueType(),searchDataRequest.getSelectedAge());
    }
}
