package com.marsview.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.marsview.controller.basic.BasicController;
import com.marsview.controller.basic.Builder;
import com.marsview.controller.basic.ResultResponse;
import com.marsview.domain.Projects;
import com.marsview.domain.Users;
import com.marsview.dto.ProjectsDto;
import com.marsview.service.ProjectsService;
import com.marsview.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>类说明</p>
 *
 * @author yangshare simayifeng@gmail.com
 * @createTime: 2024/9/27 15:11
 */
@RestController
@RequestMapping("api/project")
@Tag(name = "项目管理")
public class ProjectController extends BasicController {

    @Autowired
    private ProjectsService projectsService;

    /**
     * 分页获取项目列表
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param type     项目类型
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param keyword  关键词
     * @return 项目列表响应
     */
    @GetMapping("list")
    @Operation(summary = "分页获取项目列表")
    public ResultResponse list(
            HttpServletRequest request,
            @Parameter(description = "项目类型") @RequestParam int type,
            @Parameter(description = "当前页码") @RequestParam int pageNum,
            @Parameter(description = "每页大小") @RequestParam int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        Users users = SessionUtils.getUser(request);
        LambdaQueryWrapper<Projects> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Projects::getUserId, users.getId());
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(keyword)) {
            queryWrapper.like(Projects::getName, keyword);
        }
        if (type != 0) {
            queryWrapper.eq(Projects::getIsPublic, type);
        }

        Page<Projects> page = new Page<>(pageNum, pageSize);
        IPage<Projects> pageInfo = projectsService.page(page, queryWrapper);

        // 将 Projects 转换为 ProjectsDto
        List<ProjectsDto> records = pageInfo.getRecords().stream()
                .map(projects -> {
                    ProjectsDto dto = new ProjectsDto();
                    BeanUtils.copyProperties(projects, dto); // 使用 Apache Commons BeanUtils 进行属性复制
                    dto.setIs_edit(projects.getUserId().longValue() == users.getId().longValue());
                    return dto;
                })
                .collect(Collectors.toList());

        return Builder.of(ResultResponse::new)
                .with(ResultResponse::setData, Map.of(
                        "list", records,
                        "pageNum", pageInfo.getCurrent(),
                        "pageSize", pageInfo.getSize(),
                        "total", pageInfo.getTotal()
                ))
                .build();
    }

    /**
     * 创建项目
     *
     * @param request    HTTP 请求对象
     * @param projects 项目 DTO 对象
     * @return 创建项目响应
     */
    @PostMapping("create")
    @Operation(summary = "创建项目")
    public ResultResponse create(
            HttpServletRequest request,
            @Parameter(description = "项目 DTO 对象") @RequestBody Projects projects) {
        Users users = SessionUtils.getUser(request);
        projects.setCreatedAt(new Date());
        projects.setUserId(users.getId());
        projects.setUserName(users.getUserName());

        return getUpdateResponse(projectsService.save(projects), "项目创建失败");
    }

    /**
     * 获取页面列表
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param page_id  页面 ID
     * @return 页面列表响应
     */
    @GetMapping("/detail/{page_id}")
    @Operation(summary = "获取页面列表")
    public ResultResponse detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "页面 ID") @PathVariable("page_id") Long page_id) {
        return getResponse(projectsService.getById(page_id));
    }

    /**
     * 更新项目
     *
     * @param response  HTTP 响应对象
     * @param projects  项目对象
     * @return 更新项目响应
     */
    @PostMapping("update")
    @Operation(summary = "更新项目")
    public ResultResponse update(
            HttpServletResponse response,
            @Parameter(description = "项目对象") @RequestBody Projects projects) {
        projects.setUpdatedAt(new Date());
        return getUpdateResponse(projectsService.updateById(projects), "保存失败");
    }

    /**
     * 删除项目
     *
     * @param projects 项目对象
     * @return 删除项目响应
     */
    @PostMapping("delete")
    @Operation(summary = "删除项目")
    public ResultResponse delete(
            @Parameter(description = "项目对象") @RequestBody Projects projects) {
        return getUpdateResponse(projectsService.removeById(projects.getId()), "删除失败");
    }
}
