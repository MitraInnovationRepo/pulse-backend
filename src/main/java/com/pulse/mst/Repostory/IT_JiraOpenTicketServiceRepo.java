package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.IT_JiraOpenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface IT_JiraOpenTicketServiceRepo extends JpaRepository<IT_JiraOpenTicket,Integer> {
    @Query(value = "DROP TABLE IF EXISTS it_all_open_tickets", nativeQuery = true)
    @Modifying
    @Transactional
    void truncate();
}
