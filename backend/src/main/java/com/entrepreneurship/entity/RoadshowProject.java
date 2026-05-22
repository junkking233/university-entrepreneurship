package com.entrepreneurship.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("roadshow_project")
public class RoadshowProject {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("roadshow_id")
    private Long roadshowId;
    @TableField("project_id")
    private Long projectId;
    @TableField("presenter_id")
    private Long presenterId;
    private Integer orderNum;
    private String status;
    private String videoUrl;
    @TableField("create_time")
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoadshowId() { return roadshowId; }
    public void setRoadshowId(Long roadshowId) { this.roadshowId = roadshowId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getPresenterId() { return presenterId; }
    public void setPresenterId(Long presenterId) { this.presenterId = presenterId; }
    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
