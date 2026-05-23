package com.entrepreneurship.controller;

import com.entrepreneurship.common.PageResult;
import com.entrepreneurship.common.Result;
import com.entrepreneurship.common.SecurityInputUtil;
import com.entrepreneurship.entity.Roadshow;
import com.entrepreneurship.entity.RoadshowProject;
import com.entrepreneurship.service.ProjectService;
import com.entrepreneurship.service.RoadshowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/roadshow")
public class RoadshowController {

    private final RoadshowService roadshowService;
    private final ProjectService projectService;

    public RoadshowController(RoadshowService roadshowService, ProjectService projectService) {
        this.roadshowService = roadshowService;
        this.projectService = projectService;
    }

    @PostMapping("/create")
    public Result<Roadshow> create(@RequestBody Roadshow roadshow, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SecurityInputUtil.sanitize(roadshow);
        Roadshow result = roadshowService.create(roadshow);
        return Result.ok(result);
    }

    @GetMapping("/list")
    public Result<PageResult<Roadshow>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        PageResult<Roadshow> result = roadshowService.list(
                SecurityInputUtil.page(page),
                SecurityInputUtil.size(size),
                SecurityInputUtil.cleanStatus(status));
        return Result.ok(result);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        Roadshow roadshow = roadshowService.getById(id);
        PageResult<RoadshowProject> projects = roadshowService.listProjects(id, 1, 100);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("roadshow", roadshow);
        result.put("projects", projects);
        return Result.ok(result);
    }

    @PutMapping("/{id}")
    public Result<Roadshow> update(@PathVariable Long id, @RequestBody Roadshow roadshow) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        SecurityInputUtil.sanitizeRoadshowOptional(roadshow);
        Roadshow result = roadshowService.update(id, roadshow);
        return Result.ok(result);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        roadshowService.delete(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        Roadshow roadshow = roadshowService.getById(id);
        roadshow.setStatus(SecurityInputUtil.cleanStatus(body.get("status")));
        roadshowService.update(id, roadshow);
        return Result.ok("状态更新成功");
    }

    @PostMapping("/{id}/projects")
    public Result<RoadshowProject> addProject(@PathVariable Long id, @RequestBody RoadshowProject roadshowProject) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        roadshowProject.setRoadshowId(id);
        SecurityInputUtil.sanitize(roadshowProject);
        RoadshowProject result = roadshowService.addProject(roadshowProject);
        return Result.ok(result);
    }

    @PostMapping("/{id}/enroll")
    public Result<RoadshowProject> enroll(@PathVariable Long id, HttpServletRequest request) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        Long userId = (Long) request.getAttribute("userId");
        PageResult<com.entrepreneurship.entity.Project> projects = projectService.listByOwner(userId, 1, 1);
        if (projects.getRecords() == null || projects.getRecords().isEmpty()) {
            return Result.error("请先创建项目后再报名路演");
        }
        com.entrepreneurship.entity.Project project = projects.getRecords().get(0);
        RoadshowProject roadshowProject = new RoadshowProject();
        roadshowProject.setRoadshowId(id);
        roadshowProject.setProjectId(project.getId());
        roadshowProject.setPresenterId(userId);
        RoadshowProject result = roadshowService.addProject(roadshowProject);
        return Result.ok(result);
    }

    @DeleteMapping("/{id}/enroll")
    public Result<?> cancelEnroll(@PathVariable Long id, HttpServletRequest request) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        Long userId = (Long) request.getAttribute("userId");
        PageResult<com.entrepreneurship.entity.Project> projects = projectService.listByOwner(userId, 1, 1);
        if (projects.getRecords() == null || projects.getRecords().isEmpty()) {
            return Result.error("没有可取消的路演报名");
        }
        roadshowService.removeProject(id, projects.getRecords().get(0).getId());
        return Result.ok("取消报名成功");
    }

    @DeleteMapping("/{id}/projects/{projectId}")
    public Result<?> removeProject(@PathVariable Long id, @PathVariable Long projectId) {
        SecurityInputUtil.requirePositiveId(id, "路演ID");
        SecurityInputUtil.requirePositiveId(projectId, "项目ID");
        roadshowService.removeProject(id, projectId);
        return Result.ok("移除成功");
    }
}
