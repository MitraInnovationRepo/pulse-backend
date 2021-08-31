package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.Ortom8_JiraOpenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface OrtomOpenTicketServiceRepo extends JpaRepository<Ortom8_JiraOpenTicket,Long> {
    @Query(value = "DROP TABLE IF EXISTS ortom8_all_open_tickets", nativeQuery = true)
    @Modifying
    @Transactional
    void truncate();
}
