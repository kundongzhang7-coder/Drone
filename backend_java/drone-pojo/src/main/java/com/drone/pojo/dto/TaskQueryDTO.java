package com.drone.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskQueryDTO implements Serializable {

    private Integer page = 1;
    private Integer pageSize = 10;
    private String status;
    private String keyword;
}
