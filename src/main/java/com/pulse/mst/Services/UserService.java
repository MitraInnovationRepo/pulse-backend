package com.pulse.mst.Services;

import com.pulse.mst.Model.QuestionsRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
     ResponseEntity<?> GetUserInfo(String username, String password);

    ResponseEntity<?> GetQuestions(String username);

    ResponseEntity<?> SubmitQuestions(QuestionsRequest username);

    ResponseEntity<?> GetFeedback();

    ResponseEntity<?> GetUserById(String username, String role);


    ResponseEntity<?> changePasswordUserById(Integer userId, String password, String newPassword,Integer modifiedUserId);

    ResponseEntity<?> GetProjectRole(String username);

    ResponseEntity<?> EditUserById(Integer userId, String username, String newPassword, int roleId, String designation, String email, String projectList,Integer modifiedUserId);

    ResponseEntity<?> GetUserByIdDetails(Integer userId);

    ResponseEntity<?> DeleteUser(Integer userId,Integer modifiedUserId);

    ResponseEntity<?> GetAllFeedback();

    ResponseEntity<?> GetAFeedbackId(String feedbackId);

    ResponseEntity<?> DeleteFeedbackId(Integer userId, String feedbackId);

    ResponseEntity<?> ApprovedFeedbackId(Integer userId, String feedbackId);
}
