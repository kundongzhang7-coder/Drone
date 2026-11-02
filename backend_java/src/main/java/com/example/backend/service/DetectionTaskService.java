package com.example.backend.service;

import com.example.backend.pojo.DetectionTask;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 检测任务业务接口
 */
public interface DetectionTaskService {

    /**
     * 开始检测，创建任务记录
     *
     * @param videoName    视频名称
     * @param startTimeStr ISO 带时区的开始时间字符串
     * @return 新任务的 id
     */
    Long startTask(String videoName, String startTimeStr);

    /**
     * 结束检测，更新结束时间
     *
     * @param taskId     任务 id
     * @param endTimeStr ISO 带时区的结束时间字符串
     */
    void endTask(Long taskId, String endTimeStr);

    /**
     * 保存任务的结果视频，并把路径写入任务记录
     */
    void saveResultVideo(Long taskId, MultipartFile videoFile);

    /**
     * 获取任务的结果视频资源，不存在时返回 null
     */
    Resource getResultVideo(Long taskId);

    /**
     * 统计近 days 天每日检测次数
     */
    List<Map<String, Object>> getStats(int days);

    /**
     * 查询某一天的所有检测记录
     */
    List<DetectionTask> listByDate(LocalDate date);
}
