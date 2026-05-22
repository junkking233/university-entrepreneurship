package com.entrepreneurship.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.dto.ProjectDTO;
import com.entrepreneurship.entity.Project;

public interface ProjectService {
    Project create(Long userId, ProjectDTO projectDTO);
    Project update(Long id, ProjectDTO projectDTO);
    void delete(Long id);
    Project getById(Long id);
    PageResult<Project> list(int page, int size, String keyword, String category, String field, String status);
    PageResult<Project> listPublic(int page, int size);
    void approve(Long id);
    void reject(Long id, String reason);
    PageResult<Project> listByOwner(Long userId, int page, int size);
}
