package com.example.backend.mapper;

import com.example.backend.pojo.DetectionTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 检测任务表 Mapper（MyBatis，注解方式）
 */
public interface DetectionTaskMapper {

    /** 新增任务，自增主键回填到 id */
    @Insert("INSERT INTO detection_task (video_name, start_time, end_time, created_date, video_result_path) " +
            "VALUES (#{videoName}, #{startTime}, #{endTime}, #{createdDate}, #{videoResultPath})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DetectionTask task);

    /** 按主键更新可变字段 */
    @Update("UPDATE detection_task SET video_name = #{videoName}, start_time = #{startTime}, " +
            "end_time = #{endTime}, created_date = #{createdDate}, video_result_path = #{videoResultPath} " +
            "WHERE id = #{id}")
    int update(DetectionTask task);

    @Select("SELECT * FROM detection_task WHERE id = #{id}")
    DetectionTask selectById(@Param("id") Long id);

    /** 查询某一天的所有检测记录，按开始时间倒序 */
    @Select("SELECT * FROM detection_task WHERE created_date = #{date} ORDER BY start_time DESC")
    List<DetectionTask> selectByCreatedDate(@Param("date") LocalDate date);

    /** 统计某一天的检测次数 */
    @Select("SELECT COUNT(*) FROM detection_task WHERE created_date = #{date}")
    long countByCreatedDate(@Param("date") LocalDate date);

    /** 统计 startDate（含）之后每日检测次数（date 以 yyyy-MM-dd 字符串返回） */
    @Select("SELECT DATE_FORMAT(created_date, '%Y-%m-%d') AS date, COUNT(*) AS count " +
            "FROM detection_task " +
            "WHERE created_date >= #{startDate} " +
            "GROUP BY created_date " +
            "ORDER BY created_date ASC")
    List<Map<String, Object>> countByDateRange(@Param("startDate") LocalDate startDate);
}
