package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.FeedbackQuestions;
import com.pulse.mst.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackQuestionsRepo extends JpaRepository <FeedbackQuestions,Integer> {

    @Query("SELECT m FROM FeedbackQuestions  m WHERE m.feedbackId=?1")
    List<FeedbackQuestions> GetQuestionsAnswer(@Param("id") String id);
}
