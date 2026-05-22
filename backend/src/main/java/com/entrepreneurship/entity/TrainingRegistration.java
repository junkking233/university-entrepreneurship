package com.entrepreneurship.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("training_registration")
public class TrainingRegistration {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("training_id")
    private Long trainingId;
    @TableField("user_id")
    private Long userId;
    private String status;
    @TableField("register_time")
    private LocalDateTime registerTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTrainingId() { return trainingId; }
    public void setTrainingId(Long trainingId) { this.trainingId = trainingId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }
}
