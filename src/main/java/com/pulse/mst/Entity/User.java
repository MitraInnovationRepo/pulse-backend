package com.pulse.mst.Entity;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String userName;
    private  String email;
    private  String designation;
    private  int roleId;
    private String password;
    private int status;
    private LocalDate createdDate;
    private String projectId;
    private LocalDate modifiedDate;
    private String modifiedBy;
    private String createdBy;
    private String token;
}
