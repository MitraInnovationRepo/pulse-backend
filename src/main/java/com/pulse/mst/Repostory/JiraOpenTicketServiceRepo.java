package com.pulse.mst.Repostory;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.JsonArray;
import com.pulse.mst.Entity.JiraOpenTicket;
import com.pulse.mst.Model.SearchDataRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface JiraOpenTicketServiceRepo extends JpaRepository <JiraOpenTicket,Integer> {

    @Query(value = "DROP TABLE IF EXISTS all_open_tickets", nativeQuery = true)
    @Modifying
    @Transactional
    void truncate();

//    @Query(value = "select *from FIND_ID(:p_name,:p_key);", nativeQuery = true)
//    List<JiraOpenTicket> findP(@Param("p_name") String p_name,@Param("p_key") String p_key);


    @Query(value = "select * from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status)", nativeQuery = true)
    List<Object[]> SearchData(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);


    @Query(value = "select count(t.key) open_p1 from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t where t.priority2 ='P1' and current_status not in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> Open_P1(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);

   @Query(value = "select count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t", nativeQuery = true)
    List<Object[]> All_Issues(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);

    @Query(value = "select count(t.key) all_open from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t where  current_status not in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> All_Open(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);

     @Query(value = "select count(t.key) all_close from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t where  current_status in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> All_Close(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);


//All project details base on the role


    @Query(value = "select count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,null) t", nativeQuery = true)
    List<Object[]> Total_All_Issues(@Param("p_project_name") String  p_project_name);

    @Query(value = "select count(t.key) open_p1 from pulse.fn_get_ticket_details(:p_project_name,null,null,null) t where t.priority2 ='P1' and current_status not in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> Total_Open_P1(@Param("p_project_name") String  p_project_name);

    @Query(value = "select count(t.key) all_open from pulse.fn_get_ticket_details(:p_project_name,null,null,null) t where  current_status not in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> Total_All_Open(@Param("p_project_name") String  p_project_name);

    @Query(value = "select count(t.key) all_close from pulse.fn_get_ticket_details(:p_project_name,null,null,null) t where  current_status in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> Total_All_Close(@Param("p_project_name") String  p_project_name);


    //Search project details base on the role


    @Query(value = "select count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t", nativeQuery = true)
    List<Object[]> All_Issues_Search(@Param("p_project_name") String  p_project_name,@Param("p_status") String  p_status);

    @Query(value = "select count(t.key) open_p1 from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t where t.priority2 ='P1' and current_status not in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> Open_P1_Search(@Param("p_project_name") String  p_project_name,@Param("p_status") String  p_status);

    @Query(value = "select count(t.key) all_open from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t where  current_status not in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> All_Open_Search(@Param("p_project_name") String  p_project_name,@Param("p_status") String  p_status);

    @Query(value = "select count(t.key) all_close from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t where  current_status in('Done','Completed','Resolved','Closed')", nativeQuery = true)
    List<Object[]> All_Close_Search(@Param("p_project_name") String  p_project_name,@Param("p_status") String  p_status);



    //Proactive and Reactadata
    @Query(value = "select t.react_proact,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t group by t.react_proact", nativeQuery = true)
    List<Object[]> SearchPDData(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);

    @Query(value = "select t.react_proact,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t group by t.react_proact", nativeQuery = true)
    List<Object[]> SearchPDData2(@Param("p_project_name") String  p_project_name, @Param("p_status") String  p_status);
 //project wise
 @Query(value = "select t.project_name,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,null) t group by t.project_name", nativeQuery = true)
    List<Object[]> GetProjectWise(@Param("p_project_name") String  p_project_name);

    @Query(value = "select t.project_name,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t group by t.project_name", nativeQuery = true)
    List<Object[]> GetProjectWiseSearch1(@Param("p_project_name") String  p_project_name, @Param("p_status") String  p_status);

    @Query(value = "select t.project_name,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t group by t.project_name", nativeQuery = true)
    List<Object[]> GetProjectWiseSearch2(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);



    //priority view
    //changes 2020-03-12
    @Query(value = "select t.react_proact,t.project_name,t.priority2,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t group by t.react_proact, t.project_name, t.priority2 order by 1,2,3 asc", nativeQuery = true)
    List<Object[]> GetPriority(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);

// @Query(value = "select t.project_name,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,:p_start_date,:p_end_date,:p_status) t where  t.current_status not in('Done','Completed','Resolved','Closed') group by  t.project_name order by 1 asc", nativeQuery = true)
//    List<Object[]> GetPriority(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status);

    //changes 2020-03-12
    @Query(value = "select t.react_proact,t.project_name,t.priority2,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t group by t.react_proact, t.project_name, t.priority2 order by 1,2,3 asc", nativeQuery = true)
    List<Object[]> GetPriority2(@Param("p_project_name") String  p_project_name, @Param("p_status") String  p_status);

//    @Query(value = "select t.project_name,count(t.key) all_issues from pulse.fn_get_ticket_details(:p_project_name,null,null,:p_status) t where  t.current_status not in('Done','Completed','Resolved','Closed') group by  t.project_name order by 1 asc", nativeQuery = true)
//    List<Object[]> GetPriority2(@Param("p_project_name") String  p_project_name, @Param("p_status") String  p_status);

    //Ageing report Query
    @Query(value = "select * from  pulse.fn_get_ticket_age_details(:p_project_name,:p_start_date,:p_end_date,:p_status,:issueType,:ageName)", nativeQuery = true)
    List<Object[]> GetSearchAgeReport(@Param("p_project_name") String  p_project_name, @Param("p_start_date") LocalDate p_start_date, @Param("p_end_date") LocalDate p_end_date, @Param("p_status") String  p_status, @Param("issueType") String  issueType, @Param("ageName") String  ageName);
//Ageing report Query
    @Query(value = "select * from  pulse.fn_get_ticket_age_details(:p_project_name,null,null,:p_status,:issueType,:ageName)", nativeQuery = true)
    List<Object[]> GetSearchAgeReportBynullDate(@Param("p_project_name") String  p_project_name, @Param("p_status") String  p_status, @Param("issueType") String  issueType, @Param("ageName") String  ageName);

}
