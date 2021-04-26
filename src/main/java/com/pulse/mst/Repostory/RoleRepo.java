package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.Project;
import com.pulse.mst.Entity.Role;
import com.pulse.mst.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepo extends JpaRepository<Role,Integer> {

    @Query("SELECT m FROM Role m WHERE m.id=?1")
    List<Role> findRole(@Param("roleId") Integer roleId);


    @Query("SELECT m FROM Role m WHERE m.status=?1")
    List<Role> findActiveRole(@Param("status") Integer status);
}
