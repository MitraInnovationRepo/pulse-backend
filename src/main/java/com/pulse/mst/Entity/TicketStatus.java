package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ticket_status")
@Getter
@Setter
public class TicketStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String TicketStatus;
    int status;
}
