package com.pulse.mst.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseForList {
    private Boolean status;
    private String message;
    private Object data;

    public ApiResponseForList(Boolean status, String message,Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
