package com.pulse.mst.Model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionsRequest {
    private String username;
    private String feedback;
    private Double rate;
    private Integer userId;
    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;
    private Integer q6;
    private Integer q7;
    private String feedbackId;

}
