package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.dto.ProjectDTO;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.mapper.ProjectMapper;
import com.entrepreneurship.service.ProjectService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public Project create(Long userId, ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setCategory(projectDTO.getCategory());
        project.setField(projectDTO.getField());
        project.setTargetAmount(projectDTO.getTargetAmount());
        project.setRaisedAmount(BigDecimal.ZERO);
        project.setStatus("pending");
        project.setCoverImage(projectDTO.getCoverImage());
        project.setTeamInfo(projectDTO.getTeamInfo());
        project.setBusinessPlan(projectDTO.getBusinessPlan());
        project.setOwnerId(userId);
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.insert(project);
        return project;
    }

    @Override
    public Project update(Long id, ProjectDTO projectDTO) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        if (projectDTO.getName() != null) project.setName(projectDTO.getName());
        if (projectDTO.getDescription() != null) project.setDescription(projectDTO.getDescription());
        if (projectDTO.getCategory() != null) project.setCategory(projectDTO.getCategory());
        if (projectDTO.getField() != null) project.setField(projectDTO.getField());
        if (projectDTO.getTargetAmount() != null) project.setTargetAmount(projectDTO.getTargetAmount());
        if (projectDTO.getCoverImage() != null) project.setCoverImage(projectDTO.getCoverImage());
        if (projectDTO.getTeamInfo() != null) project.setTeamInfo(projectDTO.getTeamInfo());
        if (projectDTO.getBusinessPlan() != null) project.setBusinessPlan(projectDTO.getBusinessPlan());
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.updateById(project);
        return project;
    }

    @Override
    public void delete(Long id) {
        projectMapper.deleteById(id);
    }

    @Override
    public Project getById(Long id) {
        return projectMapper.selectById(id);
    }

    @Override
    public PageResult<Project> list(int page, int size, String keyword, String category, String field, String status) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Project::getName, keyword)
                    .or().like(Project::getDescription, keyword));
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Project::getCategory, category);
        }
        if (field != null && !field.isEmpty()) {
            wrapper.eq(Project::getField, field);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Project::getStatus, status);
        }
        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> mpPage = new Page<>(page, size);
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<Project> listPublic(int page, int size) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getStatus, "approved");
        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> mpPage = new Page<>(page, size);
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public void approve(Long id) {
        Project project = projectMapper.selectById(id);
        if (project != null) {
            project.setStatus("approved");
            project.setUpdateTime(LocalDateTime.now());
            projectMapper.updateById(project);
        }
    }

    @Override
    public void reject(Long id, String reason) {
        Project project = projectMapper.selectById(id);
        if (project != null) {
            project.setStatus("rejected");
            project.setUpdateTime(LocalDateTime.now());
            projectMapper.updateById(project);
        }
    }

    @Override
    public PageResult<Project> listByOwner(Long userId, int page, int size) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getOwnerId, userId);
        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> mpPage = new Page<>(page, size);
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }
}
