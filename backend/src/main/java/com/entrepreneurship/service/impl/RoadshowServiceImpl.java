package com.entrepreneurship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Roadshow;
import com.entrepreneurship.entity.RoadshowProject;
import com.entrepreneurship.entity.User;
import com.entrepreneurship.mapper.RoadshowMapper;
import com.entrepreneurship.mapper.RoadshowProjectMapper;
import com.entrepreneurship.mapper.UserMapper;
import com.entrepreneurship.service.RoadshowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoadshowServiceImpl implements RoadshowService {

    private final RoadshowMapper roadshowMapper;
    private final RoadshowProjectMapper roadshowProjectMapper;
    private final UserMapper userMapper;

    public RoadshowServiceImpl(RoadshowMapper roadshowMapper, RoadshowProjectMapper roadshowProjectMapper, UserMapper userMapper) {
        this.roadshowMapper = roadshowMapper;
        this.roadshowProjectMapper = roadshowProjectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Roadshow create(Roadshow roadshow) {
        roadshow.setCurrentProjects(0);
        if (roadshow.getStatus() == null || roadshow.getStatus().isEmpty()) {
            roadshow.setStatus("upcoming");
        }
        roadshow.setCreateTime(LocalDateTime.now());
        roadshowMapper.insert(roadshow);
        return enrich(roadshow);
    }

    @Override
    public Roadshow update(Long id, Roadshow roadshow) {
        Roadshow existing = roadshowMapper.selectById(id);
        if (existing != null) {
            if (roadshow.getTitle() != null) existing.setTitle(roadshow.getTitle());
            if (roadshow.getDescription() != null) existing.setDescription(roadshow.getDescription());
            if (roadshow.getStartTime() != null) existing.setStartTime(roadshow.getStartTime());
            if (roadshow.getEndTime() != null) existing.setEndTime(roadshow.getEndTime());
            if (roadshow.getLocation() != null) existing.setLocation(roadshow.getLocation());
            if (roadshow.getMaxProjects() != null) existing.setMaxProjects(roadshow.getMaxProjects());
            if (roadshow.getStatus() != null) existing.setStatus(roadshow.getStatus());
            if (roadshow.getCoverImage() != null) existing.setCoverImage(roadshow.getCoverImage());
            roadshowMapper.updateById(existing);
            return enrich(existing);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        roadshowMapper.deleteById(id);
    }

    @Override
    public Roadshow getById(Long id) {
        return enrich(roadshowMapper.selectById(id));
    }

    @Override
    public PageResult<Roadshow> list(int page, int size, String status, String keyword) {
        LambdaQueryWrapper<Roadshow> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Roadshow::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Roadshow::getTitle, keyword)
                    .or().like(Roadshow::getDescription, keyword)
                    .or().like(Roadshow::getLocation, keyword));
        }
        wrapper.orderByDesc(Roadshow::getCreateTime);
        Page<Roadshow> mpPage = new Page<>(page, size);
        Page<Roadshow> result = roadshowMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), enrich(result.getRecords()));
    }

    @Override
    @Transactional
    public RoadshowProject addProject(RoadshowProject roadshowProject) {
        roadshowProject.setStatus("pending");
        roadshowProject.setCreateTime(LocalDateTime.now());
        roadshowProjectMapper.insert(roadshowProject);

        Roadshow roadshow = roadshowMapper.selectById(roadshowProject.getRoadshowId());
        if (roadshow != null) {
            roadshowMapper.updateById(roadshow);
        }
        return roadshowProject;
    }

    @Override
    @Transactional
    public void removeProject(Long roadshowId, Long projectId) {
        LambdaQueryWrapper<RoadshowProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoadshowProject::getRoadshowId, roadshowId);
        wrapper.eq(RoadshowProject::getProjectId, projectId);
        roadshowProjectMapper.delete(wrapper);

        Roadshow roadshow = roadshowMapper.selectById(roadshowId);
        if (roadshow != null) {
            roadshowMapper.updateById(roadshow);
        }
    }

    @Override
    public PageResult<RoadshowProject> listProjects(Long roadshowId, int page, int size) {
        LambdaQueryWrapper<RoadshowProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoadshowProject::getRoadshowId, roadshowId);
        wrapper.orderByAsc(RoadshowProject::getOrderNum);
        Page<RoadshowProject> mpPage = new Page<>(page, size);
        Page<RoadshowProject> result = roadshowProjectMapper.selectPage(mpPage, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    private Roadshow enrich(Roadshow roadshow) {
        if (roadshow == null) {
            return null;
        }
        if (roadshow.getOrganizerId() != null) {
            User organizer = userMapper.selectById(roadshow.getOrganizerId());
            if (organizer != null) {
                roadshow.setOrganizerName(organizer.getName() != null && !organizer.getName().isEmpty() ? organizer.getName() : organizer.getUsername());
            }
        }
        LambdaQueryWrapper<RoadshowProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoadshowProject::getRoadshowId, roadshow.getId());
        roadshow.setCurrentProjects(roadshowProjectMapper.selectCount(wrapper).intValue());
        return roadshow;
    }

    private List<Roadshow> enrich(List<Roadshow> roadshows) {
        roadshows.forEach(this::enrich);
        return roadshows;
    }
}
