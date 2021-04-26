package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.Project;
import com.pulse.mst.Entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketStatusRepo extends JpaRepository<TicketStatus,Integer>  {
}
