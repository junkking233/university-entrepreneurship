package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.dto.ProjectDTO;
import com.entrepreneurship.entity.Project;
import com.entrepreneurship.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public Result<Project> create(@RequestBody ProjectDTO projectDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.sanitize(projectDTO);
        Project project = projectService.create(userId, projectDTO);
        return Result.ok(project);
    }

    @PutMapping("/{id}")
    public Result<Project> update(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        SecurityInputUtil.requirePositiveId(id, "项目ID");
        SecurityInputUtil.sanitizeOptional(projectDTO);
        Project project = projectService.update(id, projectDTO);
        return Result.ok(project);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "项目ID");
        projectService.delete(id);
        return Result.ok("删除成功");
    }

    @GetMapping("/{id}")
    public Result<Project> getById(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "项目ID");
        Project project = projectService.getById(id);
        return Result.ok(project);
    }

    @GetMapping("/list")
    public Result<PageResult<Project>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String status) {
        PageResult<Project> result = projectService.list(
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanText(keyword, 100, "关键词"),
                SecurityInputUtil.cleanText(category, 50, "项目分类"),
                SecurityInputUtil.cleanText(field, 50, "项目领域"),
                SecurityInputUtil.cleanStatus(status));
        return Result.ok(result);
    }

    @GetMapping("/list/public")
    public Result<PageResult<Project>> listPublic(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Project> result = projectService.listPublic(SecurityInputUtil.page(page), SecurityInputUtil.size(size));
        return Result.ok(result);
    }

    @GetMapping("/my")
    public Result<PageResult<Project>> listMyProjects(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<Project> result = projectService.listByOwner(userId, SecurityInputUtil.page(page), SecurityInputUtil.size(size));
        return Result.ok(result);
    }
}
