package com.entrepreneurship.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("consultation")
public class Consultation {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    @TableField("mentor_id")
    private Long mentorId;
    @TableField("project_id")
    private Long projectId;
    private String topic;
    private String content;
    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;
    private String status;
    private String notes;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String mentorName;
    @TableField(exist = false)
    private String projectTitle;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getUserId() { return studentId; }
    public void setUserId(Long userId) { this.studentId = userId; }
    public Long getMentorId() { return mentorId; }
    public void setMentorId(Long mentorId) { this.mentorId = mentorId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDescription() { return content; }
    public void setDescription(String description) { this.content = description; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    public LocalDateTime getAppointmentTime() { return scheduledTime; }
    public void setAppointmentTime(LocalDateTime appointmentTime) { this.scheduledTime = appointmentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getFeedback() { return notes; }
    public void setFeedback(String feedback) { this.notes = feedback; }
    public Integer getRating() { return null; }
    public void setRating(Integer rating) { }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
}
