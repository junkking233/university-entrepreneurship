package com.entrepreneurship.dto;

import java.math.BigDecimal;

public class ProjectDTO {
    private String name;
    private String description;
    private String category;
    private String field;
    private BigDecimal targetAmount;
    private String coverImage;
    private String teamInfo;
    private String businessPlan;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getTeamInfo() { return teamInfo; }
    public void setTeamInfo(String teamInfo) { this.teamInfo = teamInfo; }
    public String getBusinessPlan() { return businessPlan; }
    public void setBusinessPlan(String businessPlan) { this.businessPlan = businessPlan; }
}
