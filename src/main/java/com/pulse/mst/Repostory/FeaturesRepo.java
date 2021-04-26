package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.Features;
import com.pulse.mst.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FeaturesRepo extends JpaRepository<Features,Integer> {
    @Query("SELECT m FROM Features m WHERE m.roleId=?1")
    List<Features> findAllFeatures(@Param("roleId") Integer roleId);
}
