package com.pulse.mst.Repostory;

import com.pulse.mst.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    @Query("SELECT m FROM User m WHERE m.userName=?1")
    List<User> findUser(@Param("user_name") String user_name);

    @Query("SELECT m FROM User m WHERE m.userName=?1 AND m.status=?2")
    List<User> findActiveUser(@Param("user_name") String user_name,@Param("status") Integer status);

    @Query("SELECT m FROM User m WHERE m.id=?1")
    List<User> findUserId(@Param("userId") Integer userId);

    @Query("SELECT m FROM User m WHERE m.userName=?1 AND m.id=?2")
    List<User> findUserByName(@Param("userName") String userName,@Param("userId") Integer userId);
    @Query("SELECT m FROM User m WHERE m.userName=?1 AND m.id=?2 AND m.password=?3")

    List<User> FindCorrectPasswordUser(@Param("userName") String userName,@Param("userId") Integer userId,@Param("password") String password);
    @Query("SELECT m FROM User m WHERE m.status=?1")
    List<User> findActiveUser(@Param("status") Integer status);

    @Query("SELECT m FROM User m WHERE m.status=?1 AND m.roleId=?2")
    List<User> findActiveUserAdminEmail(@Param("status") Integer status,@Param("roleId") Integer roleId);

}
