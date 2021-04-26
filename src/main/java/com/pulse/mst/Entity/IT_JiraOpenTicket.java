package com.pulse.mst.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "it_all_open_tickets")
@Getter
@Setter
public class IT_JiraOpenTicket {

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
    LocalDateTime createdDate;
    String createdAge;
    String currentStatus;
    String currentAssigneeName;
    String CreatedMonthYear;
    LocalDateTime updated;
    String UpdatedAge;
    String timeSpent;
}
