package com.pulse.mst.Model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRequest {
    private String username;
    private Integer userId;
    private String password;
    private String email;
    private String designation;
    private String newPassword;
    private String role;
    private int roleId;
    private  String projectList;
    private  Integer modifiedUserId;
}

