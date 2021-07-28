package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ageing_issue_type")
@Getter
@Setter
public class AgeingIssueType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String issueName;
    private int status;
}
