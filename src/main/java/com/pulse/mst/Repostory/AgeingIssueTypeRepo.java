package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.AgeingIssueType;
import com.pulse.mst.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgeingIssueTypeRepo extends JpaRepository<AgeingIssueType,Integer> {

    @Query("SELECT m FROM AgeingIssueType m WHERE m.status=?1")
    List<AgeingIssueType> GetActiveAgeingIssueType(@Param("status") Integer status);
}
