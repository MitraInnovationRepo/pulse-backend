package com.pulse.mst.Model;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchDataRequest {
    List<String>  selectedStatus;
    List<String>  selectedProject;
    List<String>  selectedDateRange;
    List<String>  selectedIssueType;
    List<String>  selectedAge;

}
