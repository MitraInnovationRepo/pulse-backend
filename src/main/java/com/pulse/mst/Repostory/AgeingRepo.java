package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.Ageing;
import com.pulse.mst.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgeingRepo extends JpaRepository<Ageing,Integer> {
    @Query("SELECT m FROM Ageing m WHERE m.status=?1")
    List<Ageing> GetActiveAgeName(@Param("status") Integer status);
}
