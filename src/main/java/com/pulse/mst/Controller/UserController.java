package com.pulse.mst.Controller;

import com.pulse.mst.Entity.FeedbackQuestions;
import com.pulse.mst.Model.QuestionsRequest;
import com.pulse.mst.Model.UserRequest;
import com.pulse.mst.MstApplication;
import com.pulse.mst.Services.UserService;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    static Logger logger= LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;


    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity<?> LoginUser(@RequestBody UserRequest userRequest){
        logger.info("LoginUser-start");
        return userService.GetUserInfo (userRequest.getUsername(),userRequest.getPassword());
    }

    @RequestMapping(value = "/question",method = RequestMethod.POST)
    public ResponseEntity<?> GetQuestions(@RequestBody UserRequest userRequest){
        logger.info("GetQuestions-start");
        return userService.GetQuestions (userRequest.getUsername());
    }

    @RequestMapping(value = "/feedback",method = RequestMethod.POST)
    public ResponseEntity<?> SubmitQuestions(@RequestBody QuestionsRequest questionsRequest){
        logger.info("SubmitQuestions-start");
        return userService.SubmitQuestions (questionsRequest);
    }

    @RequestMapping(value = "/get-feedback",method = RequestMethod.GET)
    public ResponseEntity<?> GetFeedback(){
        logger.info("GetFeedback-start");
        return userService.GetFeedback ();
    }

    @RequestMapping(value = "/get-all-feedback",method = RequestMethod.GET)
    public ResponseEntity<?> GetAllFeedback(){
        logger.info("GetAllFeedback-start");
        return userService.GetAllFeedback ();
    }
    @RequestMapping(value = "/get-feedback-id",method = RequestMethod.POST)
    public ResponseEntity<?> GetFeedbackId(@RequestBody QuestionsRequest questionsRequest){
        logger.info("GetFeedbackId-start");
        return userService.GetAFeedbackId (questionsRequest.getFeedbackId());
    }

    @RequestMapping(value = "/delete-feedback-id",method = RequestMethod.POST)
    public ResponseEntity<?> DeleteFeedbackId(@RequestBody QuestionsRequest questionsRequest){
        logger.info("QuestionsRequest-start");
        return userService.DeleteFeedbackId (questionsRequest.getUserId(),questionsRequest.getFeedbackId());
    }
    @RequestMapping(value = "/approved-feedback-id",method = RequestMethod.POST)
    public ResponseEntity<?> ApprovedFeedbackId(@RequestBody QuestionsRequest questionsRequest){
        logger.info("ApprovedFeedbackId-start");
        return userService.ApprovedFeedbackId (questionsRequest.getUserId(),questionsRequest.getFeedbackId());
    }

    @RequestMapping(value = "/get-userby-id",method = RequestMethod.POST)
    public ResponseEntity<?> GetUserById(@RequestBody UserRequest userRequest){
        logger.info("GetUserById-start");
        return userService.GetUserById (userRequest.getUsername(),userRequest.getRole());
    }

    //Create new user
    @RequestMapping(value = "/edit-userby-id",method = RequestMethod.POST)
    public ResponseEntity<?> EditUserById(@RequestBody UserRequest userRequest){
        logger.info("EditUserById/Create user-start");
        return userService.EditUserById (userRequest.getUserId(),userRequest.getUsername(),userRequest.getNewPassword(),userRequest.getRoleId(),userRequest.getDesignation(),userRequest.getEmail(),userRequest.getProjectList(),userRequest.getModifiedUserId());
    }
    @RequestMapping(value = "/change -password-ById",method = RequestMethod.POST)
    public ResponseEntity<?> changePasswordUserById(@RequestBody UserRequest userRequest){
        logger.info("changePasswordUserById-start");
        return userService.changePasswordUserById (userRequest.getUserId(),userRequest.getPassword(),userRequest.getNewPassword(),userRequest.getModifiedUserId());
    }
    @RequestMapping(value = "/get-projectRole",method = RequestMethod.POST)
    public ResponseEntity<?> GetProjectRole(@RequestBody UserRequest userRequest){
        logger.info("GetProjectRole-start");
        return userService.GetProjectRole (userRequest.getUsername());
    }

    @RequestMapping(value = "/get-user",method = RequestMethod.POST)
    public ResponseEntity<?> GetUserByIdDetails(@RequestBody UserRequest userRequest){
        logger.info("GetUserByIdDetails-start");
        return userService.GetUserByIdDetails (userRequest.getUserId());
    }

    @RequestMapping(value = "/delete-user",method = RequestMethod.POST)
    public ResponseEntity<?> DeleteUser(@RequestBody UserRequest userRequest){
        logger.info("DeleteUser-start");
        return userService.DeleteUser (userRequest.getUserId(),userRequest.getModifiedUserId());
    }
}
