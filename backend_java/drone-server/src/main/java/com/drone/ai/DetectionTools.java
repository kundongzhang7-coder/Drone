package com.drone.ai;

import com.drone.common.result.PageResult;
import com.drone.pojo.dto.TaskQueryDTO;
import com.drone.pojo.vo.DetectionStatsVO;
import com.drone.pojo.vo.TaskVO;
import com.drone.service.DetectionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 提供给大模型的工具集。模型通过 function calling 决定是否调用，
 * 从而把"自然语言问题"转化为对真实业务数据的查询，这就是本项目的 Agent 能力。
 * 视觉识别仍由本地 YOLOv8 负责，大模型负责语义理解与信息编排，二者职责分离。
 */
@Component
@RequiredArgsConstructor
public class DetectionTools {

    private final DetectionTaskService detectionTaskService;

    @Tool(description = "获取无人机检测系统的整体统计数据，包括任务总数、已完成数、处理中数、累计识别无人机数和累计处理帧数")
    public DetectionStatsVO getDetectionStats() {
        return detectionTaskService.getStats();
    }

    @Tool(description = "根据任务ID查询单个无人机检测任务的详细信息，包括状态、时间、识别到的无人机数量和结果视频地址")
    public TaskVO getTaskDetail(Long taskId) {
        return detectionTaskService.getTaskById(taskId);
    }

    @Tool(description = "分页查询检测任务列表，参数 page 为页码(从1开始)，pageSize 为每页条数")
    public PageResult<TaskVO> listTasks(Integer page, Integer pageSize) {
        TaskQueryDTO query = new TaskQueryDTO();
        query.setPage(page == null ? 1 : page);
        query.setPageSize(pageSize == null ? 10 : pageSize);
        return detectionTaskService.pageTasks(query);
    }
}
