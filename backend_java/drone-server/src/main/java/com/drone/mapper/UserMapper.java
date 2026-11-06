package com.drone.mapper;

import com.drone.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    @Insert("INSERT INTO sys_user (username, password, nickname, avatar, email, phone, status, create_time, update_time) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{avatar}, #{email}, #{phone}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT id, username, password, nickname, avatar, email, phone, status, create_time, update_time " +
            "FROM sys_user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT id, username, password, nickname, avatar, email, phone, status, create_time, update_time " +
            "FROM sys_user WHERE username = #{username}")
    User findByUsername(String username);

    @Update("<script>" +
            "UPDATE sys_user" +
            "<set>" +
            "  <if test='nickname != null'>nickname = #{nickname},</if>" +
            "  <if test='avatar != null'>avatar = #{avatar},</if>" +
            "  <if test='email != null'>email = #{email},</if>" +
            "  <if test='phone != null'>phone = #{phone},</if>" +
            "  <if test='status != null'>status = #{status},</if>" +
            "  update_time = NOW()," +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(User user);
}
