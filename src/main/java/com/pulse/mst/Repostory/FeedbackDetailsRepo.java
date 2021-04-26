package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.FeedbackDetails;
import com.pulse.mst.Entity.JiraClosedTicket;
import com.pulse.mst.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackDetailsRepo extends JpaRepository <FeedbackDetails,Integer> {
   // List<FeedbackDetails> findByOrderByCreatedDateDesc();


//    @Query(value = "SELECT * from pulse.fn_get_feedback_details() ORDER By created_date DESC", nativeQuery = true)
//    List<FeedbackDetails> All_Issues_Search();


    @Query("SELECT m FROM FeedbackDetails  m WHERE m.status=?1 AND m.isApproved=?2  ORDER BY m.createdDate DESC")
    List<FeedbackDetails> findByOrderByCreatedDateDesc(@Param("status") Integer status, @Param("isApprove") Integer isApprove);

    @Query("SELECT m FROM FeedbackDetails  m  ORDER BY m.createdDate DESC")
    List<FeedbackDetails> findAllData();


    @Query("SELECT m FROM FeedbackDetails  m WHERE m.feedbackId=?1")
    List<FeedbackDetails> findById(@Param("feedbackId") String feedbackId);

}
