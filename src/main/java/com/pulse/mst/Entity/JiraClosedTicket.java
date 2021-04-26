package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "all_closed_tickets")
@Getter
@Setter
public class JiraClosedTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String projectName;
    String key;
    String priority;
    String priority2;
    String issueType;
    String summary;
    String creatorName;
    String creatorName2;
    LocalDateTime createdDate;
    String createdAge;
    String currentStatus;
    String currentAssigneeName;
    String CreatedMonthYear;
    LocalDateTime updated;
    String UpdatedAge;
    String timeSpent;
    String rootCause;
    String closureNote;
    String PlaningDate;
}
