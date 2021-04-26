package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.JiraClosedTicket;
import com.pulse.mst.Entity.JiraOpenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
public interface JiraClosedTicketServiceRepo extends JpaRepository <JiraClosedTicket,Integer> {

//    @Query(value = "select from truncate_tables()", nativeQuery = true)
//    void myProcedure();

    @Query(value = "DROP TABLE IF EXISTS all_open_tickets", nativeQuery = true)
    @Modifying
    @Transactional
    void truncate();


    List<JiraClosedTicket> findByOrderByCreatedDateDesc();
}
