package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.FeedbackDetails;
import com.pulse.mst.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionsRepo  extends JpaRepository<Question,Integer> {

}
