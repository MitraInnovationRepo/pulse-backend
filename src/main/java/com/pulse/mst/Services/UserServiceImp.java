package com.pulse.mst.Services;

import com.pulse.mst.Config.JwtTokenUtil;
import com.pulse.mst.Controller.UserController;
import com.pulse.mst.Entity.*;
import com.pulse.mst.Model.QuestionsRequest;
import com.pulse.mst.Repostory.*;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
    static Logger logger= LoggerFactory.getLogger(UserServiceImp.class);
    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    FeaturesRepo featuresRepo;
    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    QuestionsRepo questionsRepo;

    @Autowired
    FeedbackDetailsRepo feedbackDetailsRepo;

    @Autowired
    FeedbackQuestionsRepo feedbackQuestionsRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Value("${mst.site.url}")
    private String siteURL;



    public ResponseEntity<?> GetUserInfo(String username, String password){
        List<User> getUser=userRepo.findUser(username);
        logger.info("User is trying to login - User Name = "+ username);
        if(!getUser.isEmpty()){
            logger.info("User is trying to login - User Id = "+ getUser.get(0).getId());
            if(getUser.get(0).getStatus()==1) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//                String encodedPassword = passwordEncoder.encode(password);

                if (passwordEncoder.matches(password, getUser.get(0).getPassword())) {
                    List<Role> getRole = roleRepo.findRole(getUser.get(0).getRoleId());
                    List<Features> getAllFeature = featuresRepo.findAllFeatures(getUser.get(0).getRoleId());

                    String[] elements = getUser.get(0).getProjectId().split(",");
                    List<String> fixedLenghtList = Arrays.asList(elements);
                    ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);

                    JSONObject item = new JSONObject();
                    item.put("userName", getUser.get(0).getUserName());
                    item.put("userId", getUser.get(0).getId());
                    item.put("RoleName", getRole);
                    item.put("AllFeature", getAllFeature);
                    List<Project> getAll = projectRepo.findAll();
                    ArrayList<Project> list = new ArrayList<>();
                    if (!getAll.isEmpty()) {
                        for (Project val : getAll) {
                            if (listOfString.contains(Integer.toString(val.getId()))) {
                                list.add(val);
                            }
                        }
                    }
                    item.put("ProjectDetails", list);

                    String myToken;

                    try {
                        logger.info("JWT token is creating  - username = "+username);
                        myToken = TokenCreate(username, password);
                        logger.info("JWT token created  - username = "+username);
                        item.put("token", myToken);
                      //  System.out.println("myToken="+myToken);
                        getUser.get(0).setToken(myToken);
                        userRepo.save(getUser.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    logger.info("User Login Succeeded - User Id = "+ getUser.get(0).getId());
                    return ResponseEntity.ok(new ApiResponseForList(true, " User Login Succeeded", item));

                } else {
                    logger.error("User Login Failed");
                    return ResponseEntity.ok(new ApiResponseForList(false, " User Login Failed", null));

                }

            }else {
                logger.error("your account is deactivated.please contact with administer");
                return  ResponseEntity.ok(new ApiResponseForList(false, " your account is deactivated.please contact with administer",null));

            }
        }else {
            logger.error("User Login Failed");
            return  ResponseEntity.ok(new ApiResponseForList(false, " User Login Failed",null));
        }

    }

    public  ResponseEntity<?> GetQuestions (String username){
      //  System.out.println("username="+username);
        logger.info("User is  trying to Get Question Details - User name = "+ username);
        List<User> getUser=userRepo.findUser(username);
        if(!getUser.isEmpty()){
            List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());
            if(!getRole.isEmpty()){
                getRole.get(0).getRoleName();
             //   System.out.println("getRoleName="+  getRole.get(0).getRoleName());
                if ( (getRole.get(0).getRoleName()).equals("admin") || (getRole.get(0).getRoleName()).equals("client")){
                   List<Question> questionList=questionsRepo.findAll();
                    logger.info("Get Question Details Success ");
                    return  ResponseEntity.ok(new ApiResponseForList(true, " Get Question Details Success",questionList));
                }
            }
        }
        logger.error("Get Question Details failed");
        return  ResponseEntity.ok(new ApiResponseForList(false, " Get Question Details failed",null));
    }

    public  ResponseEntity<?> SubmitQuestions (QuestionsRequest questionsRequest){
        logger.info("User is  trying to Submit Questions - User name = "+ questionsRequest.getUsername());

        List<User> getUser=userRepo.findActiveUser(questionsRequest.getUsername(),1);
        List<User> findAllAdminEmail=userRepo.findActiveUserAdminEmail(1,1);
//        System.out.println("find total admin=="+findAllAdminEmail.size());
        if(!getUser.isEmpty()){
            List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());
            if(!getRole.isEmpty()){

             //   System.out.println("getRoleName="+  getRole.get(0).getRoleName());
                if ( (getRole.get(0).getRoleName()).equals("admin") || (getRole.get(0).getRoleName()).equals("client")){
                    String JobId= String.valueOf(System.currentTimeMillis());

                  //  System.out.println("JobId="+JobId);
                    FeedbackQuestions feedbackQuestions =new FeedbackQuestions();
                    feedbackQuestions.setFeedbackId(JobId);
                    feedbackQuestions.setQ1(questionsRequest.getQ1());
                    feedbackQuestions.setQ2(questionsRequest.getQ2());
                    feedbackQuestions.setQ3(questionsRequest.getQ3());
                    feedbackQuestions.setQ4(questionsRequest.getQ4());
                    feedbackQuestions.setQ5(questionsRequest.getQ5());
                    feedbackQuestions.setQ6(questionsRequest.getQ6());
                    feedbackQuestions.setQ7(questionsRequest.getQ7());
                    feedbackQuestionsRepo.save(feedbackQuestions);

                    FeedbackDetails feedbackDetails=new FeedbackDetails();
                    feedbackDetails.setUserId(getUser.get(0).getId());
                    feedbackDetails.setFeedback(questionsRequest.getFeedback());
                    feedbackDetails.setRate(questionsRequest.getRate());
                    feedbackDetails.setFeedbackId(JobId);
                    feedbackDetails.setCreatedDate(LocalDate.now());
                    feedbackDetails.setIsApproved(0);
                    feedbackDetails.setStatus(1);
//                    System.out.println("cheking  rate-------------"+questionsRequest.getRate());
                    feedbackDetailsRepo.save(feedbackDetails);
                    try{
//                        System.out.println("sending approve  email ....");
                        logger.info("sending approve  email ....");
                        URL url = new URL(siteURL);

                        emailService.sentApprovedEmail(getUser.get(0).getEmail(), "Approved Pulse Feedback ", "Hello "+getUser.get(0).getUserName()+" ,"+" \n \n Thank you very much for your feedback.this has been submitted for approved  "+url+"\n\n" +" Thank You,"+
                                "\n Mitra Managed services");
                        logger.info(" sent  approve  email .... ");
                    } catch (Exception e) {
                        logger.error("sent approve  email failed  ...."+e);
                        e.printStackTrace();
                    }
                    try{
                        //System.out.println("sending submit email......");
                        logger.info("sending submit email ....");
                        URL url = new URL(siteURL);

                        for (User getAdminEmail:findAllAdminEmail
                             ) {
//                            System.out.println("get send email id -------------"+getAdminEmail.getEmail());
                            logger.info("get send email id -------------"+getAdminEmail.getEmail());
                            emailService.sendSubmitEmail(getAdminEmail.getEmail(), "Pulse New Feedback From Client", "Hello "+"Team"+" ,"+" \n \n We have received new client feedback from "+getUser.get(0).getUserName()+". "+url+"\n\n" +" Thank You,"+
                                    "\n Mitra Managed services");
                            logger.info(" sent submit email .... ");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("sending submit email failed  ...."+e);
                    }
                    logger.info("Feedback submission Success");
                    return  ResponseEntity.ok(new ApiResponseForList(true, " Feedback submission Success",null));

                }

            }
        }
        return  ResponseEntity.ok(new ApiResponseForList(false, " Feedback submission   failed",null));

    }

    public  ResponseEntity<?> GetFeedback(){
         List<FeedbackDetails> feedback=feedbackDetailsRepo.findByOrderByCreatedDateDesc(1,1);
        JSONArray array1 = new JSONArray();
      //  System.out.println(feedback.size());
         if (!feedback.isEmpty()){

             for (FeedbackDetails row: feedback) {
                 JSONObject item = new JSONObject();
                 List<User> getUser=userRepo.findUserId(row.getUserId());

                 List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());
                 String str[] = getUser.get(0).getProjectId().split(",");
                 List<String> al = new ArrayList<String>();
                 al = Arrays.asList(str);

                 if(getRole.get(0).getRoleName().equals("client")){
                 //    System.out.println("üserid"+row.getUserId());

                     item.put("name",getUser.get(0).getUserName());
                     item.put("designation",getUser.get(0).getDesignation());
                     item.put("feedback",row.getFeedback());
                     item.put("date",row.getCreatedDate());
                     item.put("role",getRole.get(0).getRoleName());
                     item.put("rate",row.getRate());
                     List<Project> getProjecte=projectRepo.findProjectId(Integer.valueOf(al.get(0)));
                     item.put("projectName",getProjecte.get(0).getProjectName());
                     array1.add(item);
                 }
             }
         }else {
             logger.info("Get Question Details is empty");
         }
        logger.info("Get Question Details Success");
        return  ResponseEntity.ok(new ApiResponseForList(true, " Get Question Details Success",array1));
    }

    public  ResponseEntity<?> GetAllFeedback(){
        List<FeedbackDetails> feedback=feedbackDetailsRepo.findAllData();
        JSONArray array1 = new JSONArray();

        //System.out.println(feedback.size());
        if (!feedback.isEmpty()){
            int no = 0;
            for (FeedbackDetails row: feedback) {
                 no = no+1;
                JSONObject item = new JSONObject();
                // System.out.println("üserid"+row.getUserId());
                List<User> getUser=userRepo.findUserId(row.getUserId());

                List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());
                String str[] = getUser.get(0).getProjectId().split(",");
                List<String> al = new ArrayList<String>();
                al = Arrays.asList(str);


                    item.put("name",getUser.get(0).getUserName());
                    item.put("no",no);
                    item.put("designation",getUser.get(0).getDesignation());
                    item.put("email",getUser.get(0).getEmail());
                    item.put("feedback",row.getFeedback());
                    item.put("date",row.getCreatedDate());
                    item.put("rate",row.getRate());
                    item.put("modifiedBy",row.getModifiedBy());
                    item.put("modifiedDate",row.getModifiedDate());
                    item.put("feedbackId",row.getFeedbackId());
                   item.put("role",getRole.get(0).getRoleName());
                    item.put("userId",row.getUserId());
                    item.put("IsApproved",row.getIsApproved());
                    item.put("status",row.getStatus());
                    List<FeedbackQuestions> feedbackQuestions= feedbackQuestionsRepo.GetQuestionsAnswer(row.getFeedbackId());
                    List<Project> getProjecte=projectRepo.findProjectId(Integer.valueOf(al.get(0)));
                    item.put("projectName",getProjecte.get(0).getProjectName());
                    item.put("feedbackQuestions",feedbackQuestions);
                    array1.add(item);
            }

        }
        logger.info("Get Question Details Success");
        return  ResponseEntity.ok(new ApiResponseForList(true, " Get Question Details Success",array1));

    }

    public  ResponseEntity<?> DeleteFeedbackId(Integer userId, String feedbackId){

       // System.out.println("userId==="+userId);
      //  System.out.println("feedbackId==="+feedbackId);
        List<User> getUser=userRepo.findUserId(userId);
        if(!getUser.isEmpty()){
            List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());

            if(getRole.get(0).getRoleName().equals("admin")){

                List<FeedbackDetails> feedback=feedbackDetailsRepo.findById(feedbackId);


                if(feedback.get(0).getStatus()==1){
                    feedback.get(0).setStatus(0);
                    feedback.get(0).setModifiedBy(getUser.get(0).getUserName());
                    feedback.get(0).setModifiedDate(LocalDate.now());
                    feedbackDetailsRepo.save(feedback.get(0));
                    logger.info(" This feedback has been deactivated - feedback id = "+ feedback.get(0).getFeedbackId());
                    return  ResponseEntity.ok(new ApiResponseForList(true, " This feedback has been deactivated",null));

                }else {
                    feedback.get(0).setStatus(1);
                    feedback.get(0).setModifiedBy(getUser.get(0).getUserName());
                    feedback.get(0).setModifiedDate(LocalDate.now());
                    feedbackDetailsRepo.save(feedback.get(0));
                    logger.info(" This feedback has been Activated - feedback id = "+ feedback.get(0).getFeedbackId());
                    return  ResponseEntity.ok(new ApiResponseForList(true, " This feedback has been Activated",null));

                }
            }

        }else {
            logger.error(" This feedback updated failed / data not found - user id = "+userId);
        }

        return  ResponseEntity.ok(new ApiResponseForList(true, " This feedback has been update",null));


    }

    public  ResponseEntity<?> ApprovedFeedbackId(Integer userId, String feedbackId){

        List<User> getUser=userRepo.findUserId(userId);
        if(!getUser.isEmpty()){
            List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());
            if(getRole.get(0).getRoleName().equals("admin")){
                List<FeedbackDetails> feedback=feedbackDetailsRepo.findById(feedbackId);
                List<User> getUserEmail=userRepo.findUserId(feedback.get(0).getUserId());

                if(getUserEmail.get(0).getEmail()!=null){
                    feedback.get(0).setIsApproved(1);
                    feedback.get(0).setModifiedBy(getUser.get(0).getUserName());
                    feedback.get(0).setModifiedDate(LocalDate.now());
                    //send email----------------------->>>>
                    try{
                      //  System.out.println("sending approve  email ....");
                        logger.info(" sending approve  email .... ");
                        URL url = new URL(siteURL);

                        emailService.sentApprovedEmail(getUserEmail.get(0).getEmail(), "Approved Pulse Feedback ", "Hello "+getUserEmail.get(0).getUserName()+" ,"+" \n \n Your  feedback has been approved. "+url+"\n\n" +" Thank You,"+
                                "\n Mitra Managed services");
                        logger.info(" sent  approve  email .... ");
                        feedbackDetailsRepo.save(feedback.get(0));

                    } catch (Exception e) {
                        logger.error(" sending approve  email failed  .... "+e);
                        e.printStackTrace();
                    }

                }
                logger.info(" This feedback has been Approved and sent an email - FeedbackId = "+ feedback.get(0).getFeedbackId());
                    return  ResponseEntity.ok(new ApiResponseForList(true, " This feedback has been Approved and sent an email",null));
            }

        }
        return  ResponseEntity.ok(new ApiResponseForList(true, " This feedback has been Approved",null));


    }

    public ResponseEntity<?> GetAFeedbackId(String feedbackId){
      //  System.out.println("feedback id="+feedbackId);
        JSONObject item = new JSONObject();

        List<FeedbackDetails> feedback=feedbackDetailsRepo.findById(feedbackId);
        item.put("feedback",feedback.get(0).getFeedback());
        item.put("rate",feedback.get(0).getRate());
        List<FeedbackQuestions> feedbackQuestions= feedbackQuestionsRepo.GetQuestionsAnswer(feedbackId);
        item.put("Questions",feedbackQuestions.get(0));
        logger.info(" Get Question Details Success");
        return  ResponseEntity.ok(new ApiResponseForList(true, " Get Question Details Success",item));

    }

    public ResponseEntity<?> GetUserById(String username, String role){


        if (role.equals("admin")){
            JSONArray array1 = new JSONArray();
            List<User> getUser=userRepo.findAll();
            if(!getUser.isEmpty()) {
                int no = 0;
                for (User row:
                     getUser) {
                    no=no+1;
                    JSONObject item = new JSONObject();
                    // System.out.println("üserid"+row.getUserId());
                    item.put("no", Integer.parseInt(String.valueOf(no)));
                    item.put("id", row.getId());
                    item.put("createdDate", row.getCreatedDate());
                    item.put("email", row.getEmail());
                    item.put("status", row.getStatus());
                    item.put("name", row.getUserName());
                    item.put("designation", row.getDesignation());
                    item.put("modifiedBy", row.getModifiedBy());
                    item.put("modifiedDate", row.getModifiedDate());
                    item.put("createBy", row.getCreatedBy());
                    List<Role> getRole=roleRepo.findRole(row.getRoleId());
                    if(!getRole.isEmpty()){
                        item.put("role", getRole.get(0).getRoleName());
                    }else {
                        item.put("role", "Role not Assigned ");
                    }
                    String[] elements = row.getProjectId().split(",");
                    List<String> fixedLenghtList = Arrays.asList(elements);
                    ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);
                    List<Project> getAll=projectRepo.findAll();
                    ArrayList<Project> list=new ArrayList<>();
                    if (!getAll.isEmpty()){
                        for (Project val:getAll) {
                            if(listOfString.contains(Integer.toString( val.getId()))){
                                list.add(val);
                            }

                        }
                    }else {
                        item.put("ProjectList", "Project not Assigned ");

                    }
                    item.put("ProjectList", list);
                    // System.out.println("getDesignation"+getUser.get(0).getDesignation());
                    array1.add(item);

                }

                logger.info(" Get user by id Success - User Name= "+username);
                return ResponseEntity.ok(new ApiResponseForList(true, " Get user by id Success", array1));
            }else {
                logger.error("  Get user by id  failed - UserName = "+username);
                return  ResponseEntity.ok(new ApiResponseForList(false, " Get user by id  failed",null));

            }

        }else {
            int no = 1;
            JSONArray array1 = new JSONArray();
            List<User> getUser=userRepo.findUser(username);
            if(!getUser.isEmpty()) {
                JSONObject item = new JSONObject();
                // System.out.println("üserid"+row.getUserId());
                item.put("id", getUser.get(0).getId());
                item.put("no", no);
                item.put("createdDate", getUser.get(0).getCreatedDate());
               // item.put("createBy", getUser.get(0).getCreatedBy());
                item.put("email", getUser.get(0).getEmail());
               // item.put("status", getUser.get(0).getStatus());
                item.put("name", getUser.get(0).getUserName());
                item.put("designation", getUser.get(0).getDesignation());
                List<Role> getRole=roleRepo.findRole(getUser.get(0).getRoleId());
//                if(!getRole.isEmpty()){
//                    item.put("role", getRole.get(0).getRoleName());
//                }else {
//                    item.put("role", "Role not Assigned ");
//                }
                String[] elements = getUser.get(0).getProjectId().split(",");
                List<String> fixedLenghtList = Arrays.asList(elements);
                ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);
                List<Project> getAll=projectRepo.findAll();
                ArrayList<Project> list=new ArrayList<>();
                if (!getAll.isEmpty()){
                    for (Project val:getAll) {
                        if(listOfString.contains(Integer.toString( val.getId()))){
                            list.add(val);
                        }

                    }
                }else {
                    item.put("ProjectList", "Project not Assigned ");

                }
                item.put("ProjectList", list);
                // System.out.println("getDesignation"+getUser.get(0).getDesignation());
                array1.add(item);
                logger.info(" Get user by id Success - User Name= "+username);
                return ResponseEntity.ok(new ApiResponseForList(true, " Get user by id Success", array1));
            }else {
                logger.error("  Get user by id  failed - UserName = "+username);
                return  ResponseEntity.ok(new ApiResponseForList(false, " Get user by id  failed",null));

            }
        }

    }


    public ResponseEntity<?> changePasswordUserById(Integer userId, String password, String newPassword,Integer modifiedUserId) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newEndCodePassword = passwordEncoder.encode(newPassword);
        List<User> getUser = userRepo.findUserId(userId);
        List<User> modifiedUse=userRepo.findUserId(modifiedUserId);
            if (!getUser.isEmpty()) {
                if (password != null) {
                if (passwordEncoder.matches(password, getUser.get(0).getPassword())) {
                    getUser.get(0).setPassword(newEndCodePassword);
                    getUser.get(0).setModifiedBy(modifiedUse.get(0).getUserName());
                    getUser.get(0).setModifiedDate(LocalDate.now());
                      userRepo.save(getUser.get(0));
                    logger.info("  Password has been changed Successfully - userID = "+userId);
                    return ResponseEntity.ok(new ApiResponseForList(true, " Password has been changed Successfully", getUser.get(0).getUserName()));
                } else {
                    logger.error(" Current Password is wrong - userID = "+userId);
                    return ResponseEntity.ok(new ApiResponseForList(false, "  Current Password is wrong ", null));
                }
            } else {
                    getUser.get(0).setModifiedBy(modifiedUse.get(0).getUserName());
                    getUser.get(0).setModifiedDate(LocalDate.now());
                    getUser.get(0).setPassword(newEndCodePassword);
                      userRepo.save(getUser.get(0));
                    logger.info("  Password has been changed Successfully - userID = "+userId);
                    return ResponseEntity.ok(new ApiResponseForList(true, " Password has been changed Successfully", getUser.get(0).getUserName()));
                }
        }else {
                logger.error(" Password changing failed - userID = "+userId);
                return ResponseEntity.ok(new ApiResponseForList(false, " Password changing failed", null));
            }
    }

    public ResponseEntity<?> GetProjectRole(String username){
        JSONArray array1 = new JSONArray();
        JSONObject item = new JSONObject();
        List<Project> getProjecteAll=projectRepo.findActiveProject(1);
        List<Role> getRole=roleRepo.findActiveRole(1);
        item.put("ProjectList",getProjecteAll);
        item.put("RoleList", getRole);
        logger.info("  Project item get Successfully - username = "+username);
        return ResponseEntity.ok(new ApiResponseForList(true, " item get Successfully",item ));

    }

    public  ResponseEntity<?> GetUserByIdDetails(Integer userId){
        List<User> getUser=userRepo.findUserId(userId);
        logger.info("  GetUserByIdDetails item get Successfully - userId = "+userId);
        return ResponseEntity.ok(new ApiResponseForList(true, " item get Successfully",getUser ));
    }

    public  ResponseEntity<?> DeleteUser(Integer userId,Integer modifiedUserId){
        List<User> getUser=userRepo.findUserId(userId);
        List<User> modifiedUse=userRepo.findUserId(modifiedUserId);
        if(!getUser.isEmpty()){
            if(getUser.get(0).getStatus()==1){
                getUser.get(0).setModifiedBy(modifiedUse.get(0).getUserName());
                getUser.get(0).setModifiedDate(LocalDate.now());
                getUser.get(0).setStatus(0);
                userRepo.save(getUser.get(0));
                logger.info("user has been inactivated Successfully - userId = "+userId);
                return ResponseEntity.ok(new ApiResponseForList(true, " user has been inactivated Successfully",getUser ));
            }else{
                getUser.get(0).setModifiedBy(modifiedUse.get(0).getUserName());
                getUser.get(0).setModifiedDate(LocalDate.now());
                getUser.get(0).setStatus(1);
                userRepo.save(getUser.get(0));
                logger.info("user has been activated Successfully - userId = "+userId);
                return ResponseEntity.ok(new ApiResponseForList(true, " user has been activated Successfully",getUser ));
            }
        }else {
            logger.error("Deleting failed - userId = "+userId);
            return ResponseEntity.ok(new ApiResponseForList(true, " Deleting failed",getUser ));
        }

    }

    public  ResponseEntity<?> EditUserById(Integer userId, String username, String newPassword, int roleId, String designation, String email, String projectList,Integer modifiedUserId)  {

        List<User> modifiedUse=userRepo.findUserId(modifiedUserId);
        if (userId==null){
            //create token
            List<User> getUser=userRepo.findUser(username);
            if(getUser.isEmpty()){
                User user=new User();
                user.setUserName(username);
                user.setEmail(email);
                user.setStatus(1);
                user.setRoleId(roleId);
                user.setProjectId(projectList);
                user.setCreatedBy(modifiedUse.get(0).getUserName());
                user.setCreatedDate(LocalDate.now());
                user.setDesignation(designation);
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String newEndCodePassword = passwordEncoder.encode(newPassword);
                user.setPassword(newEndCodePassword);
                userRepo.save(user);
                logger.info("user successfully created  - userId = "+userId);
                return ResponseEntity.ok(new ApiResponseForList(true, "  user successfully created  ", null));

            }else {
                if(getUser.get(0).getStatus()==0){
                    logger.info("Creating user is already in our system  and inactive  - userId = "+userId);
                    return ResponseEntity.ok(new ApiResponseForList(false, " Creating user is already in our system  and inactive ", null));
                }else {
                    logger.info("Creating user is already in our system and active  - userId = "+userId);
                    return ResponseEntity.ok(new ApiResponseForList(false, " Creating user is already in our system and active", null));

                }
            }

        }else {
            List<User> getUser=userRepo.findUserId(userId);

            if(!getUser.isEmpty()){
                getUser.get(0).setEmail(email);
                getUser.get(0).setRoleId(roleId);
                getUser.get(0).setModifiedBy(modifiedUse.get(0).getUserName());
                getUser.get(0).setModifiedDate(LocalDate.now());
                getUser.get(0).setProjectId(projectList);
                getUser.get(0).setDesignation(designation);
               userRepo.save(getUser.get(0));

            }
            logger.info("user successfully updated  - userId = "+userId);
            return ResponseEntity.ok(new ApiResponseForList(true, " user successfully updated", null));

        }


    }

    private String TokenCreate(String username, String password) throws Exception {
    //    authenticate(username, password);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);

        return token;
    }

}
