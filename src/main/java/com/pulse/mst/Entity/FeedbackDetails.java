package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "feedback_details")
@Getter
@Setter
public class FeedbackDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private int id;
        private Integer userId;
        private Double rate;
        private String feedback;
        private LocalDate createdDate;
        private String feedbackId;
        private Integer status;
        private Integer isApproved;
        private LocalDate modifiedDate;
        private String modifiedBy;
}
