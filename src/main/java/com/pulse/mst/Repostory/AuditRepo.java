package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepo extends JpaRepository<Audit,Integer> {

    List<Audit> findTop1ByOrderByIdDesc();

//    @Query(value = "SELECT * FROM Audit a WHERE a.activityName.activityName=?1 " , nativeQuery=true)
    @Query(value = "SELECT * FROM audit_table arp where arp.activity_name=?1 ORDER BY arp.id DESC LIMIT 1;",nativeQuery = true)
    List<Audit> top1TicketDetails(String activityName );

}
