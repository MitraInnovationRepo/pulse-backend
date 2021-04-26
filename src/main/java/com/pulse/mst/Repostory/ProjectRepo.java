package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.JiraOpenTicket;
import com.pulse.mst.Entity.Project;
import com.pulse.mst.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Integer>  {

    @Query("SELECT m FROM Project m WHERE m.status=?1")
    List<Project> findActiveProject(@Param("status") Integer status);

    @Query("SELECT m FROM Project m WHERE m.id=?1")
    List<Project> findProjectId(@Param("id") Integer id);
}
