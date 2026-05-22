package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.entity.Roadshow;
import com.entrepreneurship.entity.RoadshowProject;

public interface RoadshowService {
    Roadshow create(Roadshow roadshow);
    Roadshow update(Long id, Roadshow roadshow);
    void delete(Long id);
    Roadshow getById(Long id);
    PageResult<Roadshow> list(int page, int size, String status);
    RoadshowProject addProject(RoadshowProject roadshowProject);
    void removeProject(Long roadshowId, Long projectId);
    PageResult<RoadshowProject> listProjects(Long roadshowId, int page, int size);
}
