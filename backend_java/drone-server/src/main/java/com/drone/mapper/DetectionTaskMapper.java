package com.drone.mapper;

import com.drone.pojo.dto.TaskQueryDTO;
import com.drone.pojo.entity.DetectionTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 检测任务 Mapper
 */
@Mapper
public interface DetectionTaskMapper {

    int insert(DetectionTask task);

    int update(DetectionTask task);

    DetectionTask findById(Long id);

    Long countList(@Param("query") TaskQueryDTO query);

    List<DetectionTask> selectPage(@Param("query") TaskQueryDTO query,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

    Long countAll();

    Long countByStatus(@Param("status") String status);

    Long sumDroneCount();

    Long sumTotalFrames();

    int batchInsert(@Param("list") List<DetectionTask> list);
}
