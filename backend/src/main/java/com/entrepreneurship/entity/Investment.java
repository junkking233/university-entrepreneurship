package com.entrepreneurship.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("investment")
public class Investment {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("investor_id")
    private Long investorId;
    @TableField("project_id")
    private Long projectId;
    private BigDecimal amount;
    private String status;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField(exist = false)
    private String projectTitle;
    @TableField(exist = false)
    private LocalDateTime investDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInvestorId() { return investorId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public LocalDateTime getInvestDate() { return investDate != null ? investDate : createTime; }
    public void setInvestDate(LocalDateTime investDate) { this.investDate = investDate; }
}
