package com.entrepreneurship.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    private String title;
    private String description;
    @TableField("business_plan")
    private String businessPlan;
    @TableField("team_info")
    private String teamInfo;
    private String category;
    private String status;
    private Integer views;
    private BigDecimal rating;
    @TableField("trust_score")
    private Integer trustScore;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBusinessPlan() { return businessPlan; }
    public void setBusinessPlan(String businessPlan) { this.businessPlan = businessPlan; }
    public String getTeamInfo() { return teamInfo; }
    public void setTeamInfo(String teamInfo) { this.teamInfo = teamInfo; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    public Integer getTrustScore() { return trustScore; }
    public void setTrustScore(Integer trustScore) { this.trustScore = trustScore; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
