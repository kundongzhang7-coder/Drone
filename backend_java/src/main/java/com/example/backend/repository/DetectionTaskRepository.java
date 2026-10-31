package com.example.backend.repository;

import com.example.backend.entity.DetectionTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DetectionTaskRepository extends JpaRepository<DetectionTask, Long> {

    long countByCreatedDate(LocalDate date);

    List<DetectionTask> findByCreatedDateOrderByStartTimeDesc(LocalDate date);

    @Query("SELECT dt.createdDate as date, COUNT(dt) as count " +
            "FROM DetectionTask dt " +
            "WHERE dt.createdDate >= :startDate " +
            "GROUP BY dt.createdDate " +
            "ORDER BY dt.createdDate ASC")
    List<Map<String, Object>> countByDateRange(@Param("startDate") LocalDate startDate);
}