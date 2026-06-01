package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.BusinessException;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.dto.ProjectDTO;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.ProjectMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.ProjectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper, UserMapper userMapper) {
        this.projectMapper = projectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Project create(Long userId, ProjectDTO projectDTO) {
        Project project = new Project();
        project.setStudentId(userId);
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setCategory(projectDTO.getCategory());
        project.setTeamInfo(projectDTO.getTeamInfo());
        project.setBusinessPlan(projectDTO.getBusinessPlan());
        project.setFundingTarget(projectDTO.getFundingTarget());
        project.setTeamSize(projectDTO.getTeamSize());
        project.setStatus("pending");
        project.setViews(0);
        project.setRating(java.math.BigDecimal.ZERO);
        project.setTrustScore(0);
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.insert(project);
        return enrich(project);
    }

    @Override
    public Project update(Long id, ProjectDTO projectDTO) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        if (projectDTO.getTitle() != null) project.setTitle(projectDTO.getTitle());
        if (projectDTO.getDescription() != null) project.setDescription(projectDTO.getDescription());
        if (projectDTO.getCategory() != null) project.setCategory(projectDTO.getCategory());
        if (projectDTO.getTeamInfo() != null) project.setTeamInfo(projectDTO.getTeamInfo());
        if (projectDTO.getBusinessPlan() != null) project.setBusinessPlan(projectDTO.getBusinessPlan());
        if (projectDTO.getFundingTarget() != null) project.setFundingTarget(projectDTO.getFundingTarget());
        if (projectDTO.getTeamSize() != null) project.setTeamSize(projectDTO.getTeamSize());
        project.setUpdateTime(LocalDateTime.now());
        projectMapper.updateById(project);
        return enrich(project);
    }

    @Override
    public void delete(Long id) {
        projectMapper.deleteById(id);
    }

    @Override
    public Project getById(Long id) {
        return enrich(projectMapper.selectById(id));
    }

    @Override
    public PageResult<Project> list(int page, int size, String keyword, String category, String field, String status) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Project::getTitle, keyword)
                    .or().like(Project::getDescription, keyword));
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Project::getCategory, category);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Project::getStatus, status);
        }
        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> mpPage = new Page<>(page, size);
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    public PageResult<Project> listPublic(int page, int size, String keyword, String category) {
        return list(page, size, keyword, category, null, "approved");
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
        wrapper.eq(Project::getStudentId, userId);
        wrapper.orderByDesc(Project::getCreateTime);

        Page<Project> mpPage = new Page<>(page, size);
        Page<Project> result = projectMapper.selectPage(mpPage, wrapper);

        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    private Project enrich(Project project) {
        if (project == null) {
            return null;
        }
        if (project.getStudentId() != null) {
            User user = userMapper.selectById(project.getStudentId());
            if (user != null) {
                project.setFounder(user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getUsername());
            }
        }
        return project;
    }

    private List<Project> enrich(List<Project> projects) {
        projects.forEach(this::enrich);
        return projects;
    }
}
