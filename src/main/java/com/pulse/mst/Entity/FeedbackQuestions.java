package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "feedback_questions")
@Getter
@Setter
public class FeedbackQuestions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String feedbackId;
    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;
    private Integer q6;
    private Integer q7;
}
