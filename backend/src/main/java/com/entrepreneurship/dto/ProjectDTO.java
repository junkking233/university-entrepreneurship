package com.entrepreneurship.dto;

import java.math.BigDecimal;

public class ProjectDTO {
    private String title;
    private String description;
    private String category;
    private String teamInfo;
    private String businessPlan;
    private BigDecimal fundingTarget;
    private Integer teamSize;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTeamInfo() { return teamInfo; }
    public void setTeamInfo(String teamInfo) { this.teamInfo = teamInfo; }
    public String getBusinessPlan() { return businessPlan; }
    public void setBusinessPlan(String businessPlan) { this.businessPlan = businessPlan; }
    public BigDecimal getFundingTarget() { return fundingTarget; }
    public void setFundingTarget(BigDecimal fundingTarget) { this.fundingTarget = fundingTarget; }
    public Integer getTeamSize() { return teamSize; }
    public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }
}
