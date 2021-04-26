package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_table")
@Getter
@Setter
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String activityName;
    LocalDateTime jobStart;
    LocalDateTime jobEnd;
    String status;
    String Message;
}
